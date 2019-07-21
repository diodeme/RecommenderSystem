import random
import math
import os
from operator import itemgetter

from settings import APP_DATA


class ItemBasedCF():
    def __init__(self):
        self.n_sim_movie = 20
        self.n_rec_movie = 10
        # 将数据集划分为训练集和测试集
        self.trainSet = {}
        self.testSet = {}
        # 用户相似度矩阵
        self.movie_sim_matrix = {}
        self.movie_popular = {}
        self.movie_count = 0
        self.trainSet_len = 0
        self.testSet_len = 0
        self.input = os.path.join(APP_DATA, 'rating-100-unique.csv')
        self.output = os.path.join(APP_DATA, 'movie_sim.csv')
        self.pivot = 0.75

        print('Similar user number = %d' % self.n_sim_movie)
        print('Recommneded movie number = %d' % self.n_rec_movie)

    def load_file(self, filename):
        with open(filename, encoding='utf-8')  as f:
            for i, line in enumerate(f):
                if i == 0:  # 去掉文件第一行的title
                    continue
                yield line.strip('\r\n')
        print('Load %s success!' % filename)

    # 针对目标用户U，找到K部相似的电影，并推荐其N部电影
    def recommend(self, user):
        self.get_dataset()
        self.get_matrix()
        K = self.n_sim_movie
        N = self.n_rec_movie
        rank = {}
        watched_movies = self.trainSet[user]
        for movie, rating in watched_movies.items():
            try:
                for related_movie, w in sorted(self.movie_sim_matrix[movie].items(), key=itemgetter(1), reverse=True)[
                                        :K]:

                    if related_movie in watched_movies:
                        continue
                    rank.setdefault(related_movie, 0)
                    rank[related_movie] += float(w) * float(rating)
            except KeyError:
                print("catch an exception")

        return sorted(rank.items(), key=itemgetter(1), reverse=True)[:N]

    # 获取文件中的数据并保存到数据集中
    def get_dataset(self):
        for line in self.load_file(self.input):
            user = line.split(',')[0]
            movie = line.split(',')[3]
            rating = str(float(line.split(',')[4]) / 5)
            if (random.random() < self.pivot):
                self.trainSet.setdefault(user, {})
                self.trainSet[user][movie] = rating
                self.trainSet_len += 1
            else:
                self.testSet.setdefault(user, {})
                self.testSet[user][movie] = rating
                self.testSet_len += 1
        print('Split trainingSet and testSet success!')
        print('TrainSet = %s' % self.trainSet_len)
        print('TestSet = %s' % self.testSet_len)

    #从文件中读取相似度矩阵
    def get_matrix(self):
        for line in self.load_file(self.output):
            m1, m2, sim = line.split(',')
            self.movie_sim_matrix.setdefault(m1, {})
            self.movie_sim_matrix[m1][m2] = sim
        return
    #产生相似度矩阵
    def calc_user_sim(self):
        # 计算电影之间的相似度
        for user, movies in self.trainSet.items():
            for movie in movies:
                if movie not in self.movie_popular:
                    self.movie_popular[movie] = 0
                self.movie_popular[movie] += 1
        movie_count = len(self.movie_popular)

        for user, movies in self.trainSet.items():
            for m1 in movies:
                for m2 in movies:
                    if m1 == m2:
                        continue
                    self.movie_sim_matrix.setdefault(m1, {})
                    self.movie_sim_matrix[m1].setdefault(m2, 0)
                    self.movie_sim_matrix[m1][m2] += 1
        #同时出现在某用户的次数除以单独出现的次数
        import csv
        with open(self.output, 'a+', encoding='utf-8', newline='') as fs:
            csv_write = csv.writer(fs)
            for m1, related_movies in self.movie_sim_matrix.items():
                #  print(m1)
                for m2, count in related_movies.items():
                    # 注意0向量的处理，即某电影的用户数为0
                    if self.movie_popular[m1] == 0 or self.movie_popular[m2] == 0:
                        self.movie_sim_matrix[m1][m2] = 0
                    else:
                        self.movie_sim_matrix[m1][m2] = count / math.sqrt(
                            self.movie_popular[m1] * self.movie_popular[m2])
                    data = [m1, m2, self.movie_sim_matrix[m1][m2]]
                    csv_write.writerow(data)
        fs.close()

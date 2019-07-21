# coding = utf-8

# 基于用户的协同过滤推荐算法实现
import random
import os
import math
from operator import itemgetter


from settings import APP_DATA
class UserBasedCF():
    # 初始化相关参数
    def __init__(self):
        # 找到与目标用户兴趣相似的20个用户，为其推荐10部电影
        self.n_sim_user = 20
        self.n_rec_movie = 10

        # 将数据集划分为训练集和测试集
        self.trainSet = {}
        self.testSet = {}

        # 用户相似度矩阵
        self.user_sim_matrix = {}
        self.movie_count = 0

        self.input = os.path.join(APP_DATA, 'rating-100-unique.csv')
        self.output = os.path.join(APP_DATA, 'user_sim.csv')
        self.pivot = 0.75
        print('Similar user number = %d' % self.n_sim_user)
        print('Recommneded movie number = %d' % self.n_rec_movie)


    # 读文件得到“用户-电影”数据
    def get_dataset(self):
        trainSet_len = 0
        testSet_len = 0
        for line in self.load_file(self.input):
            user = line.split(',')[0]
            movie = line.split(',')[3]
            rating = line.split(',')[4]
            if random.random() < self.pivot:
                self.trainSet.setdefault(user, {})
                self.trainSet[user][movie] = rating
                trainSet_len += 1
            else:
                self.testSet.setdefault(user, {})
                self.testSet[user][movie] = rating
                testSet_len += 1
        print('Split trainingSet and testSet success!')
        print('TrainSet = %s' % trainSet_len)
        print('TestSet = %s' % testSet_len)


    # 读文件，返回文件的每一行
    def load_file(self, filename):
        with open(filename,encoding='utf-8') as f:
            for i, line in enumerate(f):
                if i == 0:  # 去掉文件第一行的title
                    continue
                yield line.strip('\r\n')
        print('Load %s success!' % filename)


    # 计算用户之间的相似度
    def calc_user_sim(self):
        # 构建“电影-用户”倒排索引
        movie_user = {}
        for user, movies in self.trainSet.items():
            for movie in movies:
                if movie not in movie_user:
                    movie_user[movie] = set()
                movie_user[movie].add(user)

        self.movie_count = len(movie_user)

        print('Build user co-rated movies matrix ...')
        for movie, users in movie_user.items():
            for u in users:
                for v in users:
                    if u == v:
                        continue
                    self.user_sim_matrix.setdefault(u, {})
                    self.user_sim_matrix[u].setdefault(v, 0)
                    self.user_sim_matrix[u][v] += 1
        print('Build user co-rated movies matrix success!')

        # 计算相似性
        print('Calculating user similarity matrix ...')
        import csv
        with open(self.output, 'a+', encoding='utf-8', newline='') as fs:
            csv_write = csv.writer(fs)
            for u, related_users in self.user_sim_matrix.items():
                for v, count in related_users.items():
                    self.user_sim_matrix[u][v] = count / math.sqrt(len(self.trainSet[u]) * len(self.trainSet[v]))
                    data = [u, v, self.user_sim_matrix[u][v]]
                    csv_write.writerow(data)
            print('Calculate user similarity matrix success!')
        fs.close()

    def get_matrix(self):
        for line in self.load_file(self.output):
            m1,m2,sim=line.split(',')
            self.user_sim_matrix.setdefault(m1, {})
            self.user_sim_matrix[m1][m2]=sim
        return
    # 针对目标用户U，找到其最相似的K个用户，产生N个推荐
    def recommend(self, user):
        self.get_dataset()
        self.get_matrix()
        K = self.n_sim_user
        N = self.n_rec_movie
        rank = {}
        watched_movies = self.trainSet[user]


        for v, wuv in sorted(self.user_sim_matrix[user].items(), key=itemgetter(1), reverse=True)[0:K]:
            for movie in self.trainSet[v]:
                if movie in watched_movies:
                    continue
                rank.setdefault(movie, 0)
                rank[movie] += float(wuv)
        return sorted(rank.items(), key=itemgetter(1), reverse=True)[0:N]

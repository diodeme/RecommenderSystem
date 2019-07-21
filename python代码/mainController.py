from pandas import DataFrame

from service import recService
from service import csvService
from service import movieService
from flask import Flask, jsonify
from flask_cors import CORS
app = Flask(__name__)
CORS(app, resources=r'/*')
@app.route('/<int:userID>/favor')
def favor(userID):
    '''
    outfile=csvService.userMovie(userID)
    array = outfile['电影名'].values
    ratings=outfile['评分'].values
    idList=movieService.generate_ID(array)
    merge_dt_dict = {'ID': idList,
                     'ratings': ratings}
    data_df = DataFrame(merge_dt_dict)
    '''
    return csvService.userMovie(userID)
@app.route('/rec/<string:rec>/<string:userId>')
def rec(rec,userId):
    jsonArr = jsonify(recService.rec(rec,userId))
    return jsonArr
if __name__ == '__main__':
    app.run(debug=True)
import pandas as pd
import os
from settings import APP_DATA
def userMovie(userID):
    data = pd.read_csv(os.path.join(APP_DATA, 'rating-100-unique.csv'))
    outfile = data[data['userId'] == userID][['doubanId','rating']]
    return outfile.to_json(orient='records')



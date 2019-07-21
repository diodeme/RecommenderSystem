import os,sys
parentdir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

sys.path.insert(0,parentdir)

import os,sys
parentdir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

sys.path.insert(0,parentdir)

from rec.userCF import UserBasedCF
from rec.itemCF import ItemBasedCF



def rec(rec,user):
    a=None
    if(rec=='user'):
        a= UserBasedCF()
    if(rec=='item'):
        a=ItemBasedCF()
    result=a.recommend(user)
    return result



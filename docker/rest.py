#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Date    : 2018-01-04
# @Author  : zhangmingcheng
 
import web
from PIL import Image
import imagehash
import glob
import json
import os,sys
 
urls=(
    '/images/getImagesDhash','get_imagesdhash',
    '/images/getImageDhash','get_imagedhash',
    '/images/getRandom','get_random',
)
app = web.application(urls,globals())
 
class Dhash(object):
    def __init__(self, name, dhash): 
          self.name = name 
          self.dhash = dhash
 
class get_imagesdhash:
    def GET(self):
                path = web.input().path
                dhashs = []
        for imagePath in glob.glob(path + "/*.*"):
            image = Image.open(imagePath)
                h = str(imagehash.dhash(image))
                    filename = imagePath[imagePath.rfind("/") + 1:]
                    dhash = Dhash(filename,h)
                    dhashs.append(dhash)
        return json.dumps(dhashs, default=lambda o: o.__dict__, sort_keys=True, indent=4)  
class get_imagedhash:
        def GET(self):
                path = web.input().path
                image = Image.open(path)
                h = str(imagehash.dhash(image))
                return h 
class get_random:
        random = 0
 
        def GET(self):
               randoms = [1000,2000,3000,4000,5000]
               result = randoms[get_random.random]
               get_random.random = (get_random.random + 1)%5
               return result;
                
if __name__ == '__main__':
        app.run()
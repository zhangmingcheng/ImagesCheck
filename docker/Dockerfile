FROM lmurawsk/python2.7
RUN pip install -i https://pypi.tuna.tsinghua.edu.cn/simple imagehash
ADD web.py-0.38 /opt/web.py-0.38 
WORKDIR cd /opt/web.py-0.38/
RUN easy_install web.py
RUN mkdir -p /root/imagedhash
ADD rest.py /root/imagedhash
WORKDIR /root/imagedhash
EXPOSE 8080
CMD python /root/imagedhash/rest.py

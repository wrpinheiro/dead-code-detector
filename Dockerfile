FROM openjdk:8u121-jdk

ENV UNDERSTAND_MINOR_VERSION 844
ENV UNDERSTAND_VERSION 4.0.$UNDERSTAND_MINOR_VERSION

RUN apt-get update && \
  apt-get install -y libglib2.0-0 libxext6 wget && \
  wget -qO- http://builds.scitools.com/all_builds/b$UNDERSTAND_MINOR_VERSION/Understand/Understand-$UNDERSTAND_VERSION-Linux-64bit.tgz \
    | tar -zxv && \
  apt-get clean

RUN echo "Server: scitools-license.devfactory.com 00000000 9000" > scitools/conf/license/locallicense.dat

ENV LD_LIBRARY_PATH /usr/lib/jvm/java-8-oracle/jre/lib/amd64:/scitools/bin/linux64
ENV PYTHONPATH /scitools/bin/linux64/python

ENV PATH $PATH:/scitools/bin/linux64:/app/scripts

# custom app envs
ENV ANALYZER_REPO_DIR /data/
ENV APP_SCRIPTS_DIR /app/scripts/
ENV SCITOOLS_HOME /scitools/bin/linux64

RUN mkdir /data

COPY build/libs/dead-code-detection-service.jar /app/dead-code-detection-service.jar
COPY scripts /app/scripts

RUN mkdir /var/log/deadcodedetection

ENV JAVA_XMX 1024M

EXPOSE 80 8080
ENTRYPOINT exec java -Xmx$JAVA_XMX -jar /app/dead-code-detection-service.jar

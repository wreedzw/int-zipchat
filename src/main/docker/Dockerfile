FROM zipwhip/oracle-java8-base:alpine-jre-9
WORKDIR /root
ARG jarName
ADD $jarName app.jar
COPY startup.sh /startup.sh
RUN chmod +x /startup.sh

ENTRYPOINT ["/usr/bin/dumb-init", "--"]
CMD ["/startup.sh"]

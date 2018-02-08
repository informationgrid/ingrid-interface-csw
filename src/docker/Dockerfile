FROM docker-registry.wemove.com/ingrid-java
ENV INGRID_USER=root
ENV RUN_DIRECTLY=true

ADD ingrid-interface-csw-*-installer.jar /

RUN unzip ingrid-interface-csw-*-installer.jar -d /tmp \
    && mkdir -p /opt/ingrid/ingrid-interface-csw \
    && cp -R /tmp/ingrid-interface-csw-*/* /opt/ingrid/ingrid-interface-csw/ \
    && rm -Rf /tmp/* \
    && rm ingrid-*.jar

WORKDIR /opt/ingrid/ingrid-interface-csw
EXPOSE 8084

CMD /bin/sh start.sh start
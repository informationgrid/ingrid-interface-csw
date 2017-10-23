FROM docker-registry.wemove.com/ingrid-java
ENV INGRID_USER=root
ENV RUN_DIRECTLY=true
ENV ADMIN_PASSWORD=admin
ENV BUS_URL=/ingrid-group:ibus
ENV SERVER_PORT=8084
ENV MAX_RETURNED_HITS=100
ENV SERVER_INTERFACE_HOST=baw-dmqs.informationgrid.eu
ENV SERVER_INTERFACE_PORT=80
ENV SERVER_INTERFACE_PATH=csw

ADD ingrid-interface-csw-*-installer.jar /

RUN unzip ingrid-interface-csw-*-installer.jar -d /tmp \
    && mkdir -p /opt/ingrid/ingrid-interface-csw \
    && cp -R /tmp/ingrid-interface-csw-*/* /opt/ingrid/ingrid-interface-csw/ \
    && echo "server.port=$SERVER_PORT\nmax.returned.hits=$MAX_RETURNED_HITS\nserver.interface.host=$SERVER_INTERFACE_HOST\nserver.interface.port=$SERVER_INTERFACE_PORT\nserver.interface.path=$SERVER_INTERFACE_PATH" > /opt/ingrid/ingrid-interface-csw/conf/config.override.properties \
    && cd /opt/ingrid/ingrid-interface-csw/ \
    && /bin/sh start.sh resetPassword $ADMIN_PASSWORD \
    && rm -Rf /tmp/* \
    && rm /ingrid-*.jar

WORKDIR /opt/ingrid/ingrid-interface-csw
EXPOSE 8084

CMD /bin/sh start.sh start
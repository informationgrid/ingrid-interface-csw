FROM docker-registry.wemove.com/ingrid-java
ENV INGRID_USER=ingrid
ENV RUN_DIRECTLY=true

ADD ingrid-interface-csw-*-installer.jar /

SHELL ["/bin/bash", "-o", "pipefail", "-c"]
RUN unzip ingrid-interface-csw-*-installer.jar -d /tmp \
    && mkdir -p /opt/ingrid/ingrid-interface-csw \
    && cp -R /tmp/ingrid-interface-csw-*/* /opt/ingrid/ingrid-interface-csw/ \
    && rm -Rf /tmp/* \
    && rm ingrid-*.jar \
    && useradd --user-group --system --create-home -u 3073 ingrid \
    && echo 'ingrid:JZHkpLMQ0fjKl0X0i2oE' | chpasswd \
    && chown -R ingrid /opt/ingrid/ingrid-interface-csw \
    && chmod -R u+rwX /opt/ingrid/ingrid-interface-csw

USER ingrid

WORKDIR /opt/ingrid/ingrid-interface-csw
EXPOSE 8080/tcp

CMD /bin/sh start.sh start
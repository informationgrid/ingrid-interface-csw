FROM alpine AS builder

ADD ingrid-interface-csw-*-installer.jar /
RUN unzip -q ingrid-interface-csw-*-installer.jar -d /tmp \
    && export INSTALL_DIR=`ls -d1 /tmp/ingrid-interface-*` \
    && sed -i -r 's/(<AppenderRef.*\/>)/\1<AppenderRef ref="Console" \/>/g' $INSTALL_DIR/conf/log4j2.xml

FROM docker-registry.wemove.com/ingrid-java:jre-8-slim
ENV INGRID_USER=ingrid
ENV RUN_DIRECTLY=true

# user, group ingrid (1000:1000) is created in base image
COPY --chown=1000:1000 --from=builder /tmp/ingrid-interface-csw-* /opt/ingrid/ingrid-interface-csw/

USER ingrid

WORKDIR /opt/ingrid/ingrid-interface-csw
EXPOSE 8080/tcp

CMD /bin/sh start.sh start

#
# Copyright (c) 2020 Gobierno de España
#
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.
#
# SPDX-License-Identifier: MPL-2.0
#

FROM adoptopenjdk:11-jre-openj9 as builder
WORKDIR /authentication
COPY  [ "${project.artifactId}-${project.version}-exec.jar", "app.jar" ]
RUN java -Djarmode=layertools -jar app.jar extract

FROM adoptopenjdk:11-jre-openj9
WORKDIR /authentication

# Metadata
LABEL module.vendor="Radar-COVID"         \
      module.maintainer="SEDIAgob"        \
      module.name="${project.artifactId}" \
      module.version="${project.version}"

VOLUME [ "/tmp" ]

ARG AWS_ACCESS_KEY
ARG AWS_SECRET_KEY
ARG AWS_PARAMSTORE_ENABLED

ENV AWS_ACCESS_KEY_ID ${AWS_ACCESS_KEY}
ENV AWS_SECRET_KEY ${AWS_SECRET_KEY}
ENV AWS_PARAMSTORE_ENABLED ${AWS_PARAMSTORE_ENABLED}

ENV JAVA_TOOL_OPTIONS $JAVA_TOOL_OPTIONS -Xms256M -Xmx1G
ENV SERVER_PORT 8080

EXPOSE ${SERVER_PORT}

COPY --from=builder authentication/dependencies/ ./
COPY --from=builder authentication/spring-boot-loader/ ./
COPY --from=builder authentication/snapshot-dependencies/ ./
COPY --from=builder authentication/company-dependencies/ ./
COPY --from=builder authentication/application/ ./

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=${build.profile.id}", "org.springframework.boot.loader.JarLauncher"]

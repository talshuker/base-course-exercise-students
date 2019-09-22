FROM docker-registry-default.app.osft/openshift/rhel-76-nodejs:10.14.2

#set working directory
WORKDIR /user/src/ofek

COPY . /user/src/ofek/

#RUN
CMD  node ./node_modules/ts-node/dist/bin.js src/index.ts
FROM redis:7.2.5-alpine

RUN mkdir -p /opt/certs

COPY ./certs/* /opt/certs

RUN chown -R redis:redis /opt/certs

CMD ["redis-server", "--save", "20", "1", "--loglevel", "warning", "--requirepass", "wibble", \
     "--tls-port", "6379", "--port", "0", "--tls-cert-file", "/opt/certs/docker_dev.pem", \
     "--tls-key-file", "/opt/certs/docker_dev-key.pem", "--tls-ca-cert-file", "/opt/certs/orcid_rsa_2022.crt", \
     "--tls-auth-clients", "no", "--tls-replication", "yes"]

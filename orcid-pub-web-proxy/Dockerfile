# debian:buster-20210408-slim
FROM nginx:1.18.0

RUN mkdir -p /etc/nginx/snippets

RUN mkdir -p /etc/nginx/certs

COPY orcid-pub-web-proxy/nginx/nginx.conf /etc/nginx/nginx.conf

COPY orcid-pub-web-proxy/nginx/conf.d/* /etc/nginx/conf.d/

EXPOSE 80

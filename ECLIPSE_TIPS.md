# Eclipse Tips

# I hate restarting for every class change
Use spring-loaded, it will allow reloading of classes
[http://vitalflux.com/configure-springloaded-eclipse-dynamic-web-project/](http://vitalflux.com/configure-springloaded-eclipse-dynamic-web-project/)

After getting spring loaded working. You might need to disable auto reload to keep tomcat restarting with andy new change. You need to disable "auto reload"
[https://github.com/HotswapProjects/HotswapAgent/wiki/Eclipse-setup](https://github.com/HotswapProjects/HotswapAgent/wiki/Eclipse-setup)


# Search finds the same file times
Close the top level project. In the Navigator view right click on the maven parent "ORCID-Source" and click close.

# Eclipse doesn't see file changes and I alwasy have to refresh
Enable Refresh using native hooks or polling. File updates can lag 3 to 5 seconds.
https://stackoverflow.com/questions/13470311/eclipse-refresh-files-edited-by-external-editor

# Eclipse doesn't highlight typscript
https://github.com/palantir/eclipse-typescript




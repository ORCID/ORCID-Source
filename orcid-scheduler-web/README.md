# ORCID-Scheduler-Web
Welcome to the ORCID Scheduler project. Here we configure all task that we will like to run in a scheduled basis.

## How does it work?  

There are some tasks at the [ORCID's Core project](https://github.com/ORCID/ORCID-Source/tree/master/orcid-core) to run in a scheduled basis, so, they are registered in the list of scheduled tasks in the [scheduler config file](https://github.com/ORCID/ORCID-Source/blob/master/orcid-scheduler-web/src/main/resources/orcid-scheduler-web-context.xml), those tasks can be of two different types:

* fixed-delay: An interval-based trigger where the interval is measured from the completion time of the previous task. The time unit value is measured in milliseconds.
* cron: A cron-based trigger. See the [CronSequenceGenerator]( http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html) JavaDoc for example patterns.

When a task is registered here, the scheduler engine will process it and run it following the rule specified for that task.

## How to register a new scheduled task?

To register a new scheduled task, you first need to be sure that:

###*Prerequisites*

1. The class containing the scheduled task should be at the [ORCID's Core project](https://github.com/ORCID/ORCID-Source/tree/master/orcid-core) and the method that will run should be public.
2. There should be a bean associated with that class in the [context config file](https://github.com/ORCID/ORCID-Source/blob/master/orcid-core/src/main/resources/orcid-core-context.xml)
3. There should be acouple of [unit tests](http://junit.org/) in the [tests directory](https://github.com/ORCID/ORCID-Source/tree/master/orcid-core/src/test/java/org/orcid/core) that ensures you cron job runs as expected.

Then, when you are sure that your class is ready and that none of the unit tests got broken, you can register your scheduled task like this:

###*Registering the task*

Add your task to the [orcid-scheduler-web-context.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-scheduler-web/src/main/resources/orcid-scheduler-web-context.xml) file, as the last element of the task:scheduled-tasks element and looking like this:
```XML
<task:scheduled ref="bean_name" method="method_name" type="cron_or_time_in_millis"/>
```

Where:
* bean_name: the bean name defined in [context config file](https://github.com/ORCID/ORCID-Source/blob/master/orcid-core/src/main/resources/orcid-core-context.xml)
* method_name: the method that will be triged by the scheduler
* type: 
  * cron: A cron-based trigger. See the [CronSequenceGenerator](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html) JavaDoc for example patterns.
  * fixed-delay: An interval-based trigger where the interval is measured from the completion time of the previous task. The time unit value is measured in milliseconds.
* cron_or_time_in_millis: A cron expression or a number of milliseconds based on the type you selected

# Getting Support

If you are experience problems using ORCID you can check our [help page](http://orcid.org/help). 

# Contributing
Pull requests are welcome; See [CONTRIBUTING.md](CONTRIBUTING.md)

# Development Environment Setup
See [DEVSETUP.md](https://github.com/ORCID/ORCID-Source/blob/master/DEVSETUP.md)

# License
See [LICENSE.md](https://github.com/ORCID/ORCID-Source/blob/master/LICENSE.md)

# Contributors
See [CREDITS.md](https://github.com/ORCID/ORCID-Source/blob/master/CREDITS.md)

# Projects
See [PROJECTS.md](https://github.com/ORCID/ORCID-Source/blob/master/PROJECTS.md)


# ORCID-Scheduler-Web
Welcome to the ORCID Scheduler project. Here we configure all task that we will like to run in a scheduled basis.

# How does it work?  

If you have a task in [ORCID's Core project](https://github.com/ORCID/ORCID-Source/tree/master/orcid-core) that you would like to configure as a scheduler task:

- Make sure you defined a bean [here](https://github.com/ORCID/ORCID-Source/blob/master/orcid-core/src/main/resources/orcid-core-context.xml) for the class that have the method you want to schedule, and, that the method is public.
- Add the task definition to the [scheduler config file](https://github.com/ORCID/ORCID-Source/blob/master/orcid-scheduler-web/src/main/resources/orcid-scheduler-web-context.xml) as the last entrance of the scheduled tasks list (the code inside the <task:scheduled-tasks scheduler="scheduler"> tag) 
  - The task should look like this:





 
 
# License
See [LICENSE.md](https://github.com/ORCID/ORCID-Work-in-Progress/blob/master/LICENSE.md)


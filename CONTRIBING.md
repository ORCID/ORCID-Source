The ORCID Team is always excited to get contributions. Checkout our pointers below:

1.  **Understand the basics**	

    Not sure what a pull request is, or how to submit one?  Take a look at GitHub's
    excellent [Collaborating Documenation](https://help.github.com/categories/63/articles) first.


* **Discuss non-trivial contribution ideas with committers**

    If you're considering anything more than correcting a typo or fixing a minor
    bug, please discuss it on the [ORCID Technical Community Google Group]
    (https://groups.google.com/forum/?fromgroups#!forum/orcid-technical-community) 
    mailing list before submitting a pull request. 


* **Submit JUnit test cases for all behavior changes**

    Search the codebase to find related unit tests and add additional @Test methods
    within. 


* **Run all tests prior to submission**

    Make sure that all tests pass prior to submitting your pull request. See the 
   [DEVSETUP.md Testing](DEVSETUP.md#integration-tests)


* **Squash commits**

    Use `git rebase --interactive`, `git add --patch` and other tools to "squash"
    multiple commits into atomic changes.


* **Also Make it fun :-)**
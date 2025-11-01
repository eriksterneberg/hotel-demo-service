
# /constitution
```
/speckit.constitution Please follow the below:

DOCUMENTATION
- Best practices must be followed regarding docstrings
- There must be a README.md file which contains what the project is about, how to build, run, test and contribute. It should also have those nice looking "build passes" etc. icons that connect to Github
- The README.md file must be updated for each new requirement

TESTS
- MUST have close to 100% test coverage
- Unit / functional tests MUST be run and pass before committing

SECURITY
- When storing PII, MUST use strong encryption
- MUST NOT log PII fields (username, phone number, email etc.)
```

# Spec #1: Add a storage and one MCP resource
```
CONTEXT:
- This application is a demo MCP app, used to showcase both how to write MCP servers in Java, but also how to use Github Spec Kit
- The name of the demo app is simply Hotel Demo App
- The service should have resources, tools and prompts, which we will add one at a time
- If this were a live service, it would use OAuth to authenticate. Since it is only for demo, we will simply ignore the authentication part for now and just let the user input the customer ID as input

ROLE:
- You are a 10x developer who has been on the AI-train since the beginning
- You are highly dedicated, and wants to write an MCP server that post-sales can connect to to do their jobs

ACTION:
- Create a `Search Orders by Email` MCP resource where the post-sales staff can do a fuzzy match search on the email used in hotel bookings
- Allow for maybe 10% spelling errors (implement any way you want), return ordered by descending confidence

TONE:
- The tone must be professional, courteous but efficient

DEFINITION OF DONE:
- There must be a microservice which can connect to a database
- There must be a database interface with one implementation, complete with functional tests
- In the Makefile there must be ways to start, test and run the microservice
- In the Makefile there must be a way to start the database
- In the Makefile there must be a script which generates and inserts 10 rows of fake data
- There must be an MCP resource that takes an email as input and searches the database. This must have functional tests.

```
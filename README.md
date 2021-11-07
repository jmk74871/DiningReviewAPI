<h1> Dining Review API </h1>

<p>Dining Review API is a basic RestAPI roughly based on the requirements given in a Milestone project 
in the Building <a href="https://www.codecademy.com/learn/paths/create-rest-apis-with-spring-and-java">
REST APIs with Java and Spring Course on CodeCademy</a>. The overall goal is to practice my skills working with the Springframework, Kotlin and Gradle.</p>

<p>Although I finished the course using Java as the programming language and Maven as a build tool 
I decided to build this project using Kotlin and since Gradle allows settings to be written in Kotlin 
I went with Gradle instead of Maven for the build tool.</p>

<p>This documentation should therefore primarily be a reference for my self 
- sometimes it is helpful to write stuff down - as well as a way to gain a quick overview of the 
project for anyone who's interested.</p>

<br><br>
<h2> API Endpoints </h2>

<p>This part will give a brief overview of the API endpoints implemented so far. By default the API will be available under Port 8080 on localhost. So the endpoint url gets added to http://localhost:8080</p>

<br>
<h3>Public Endpoints</h3>

<p>These Endpoints are available to everyone without creating a user account and performing a login to perform GET requests. </p>

<h4>GET `.../api/v1/` </h4>
<p>Returns a list of json object containing information on all the restaurants known to the API.</p>

<h4>GET `.../api/v1/{id}`</h4>
<p>Returns a json object containing information on the restaurant with the given ID.</p>

<h4>GET `.../api/v1/reviews/{id}`</h4>
<p>Returns a list of json object containing information all the approved reviews regarding the restaurant with the given ID.</p>

<h4>POST `.../api/v1/signup`</h4>
<p>Expects a json object as request body representing a new user. Checks if the username is available, hashes the password and creates the user account.</p>

<h4>POST `.../api/v1/login`</h4>
<p>This is the endpoint to login with an existing user account. Checks if there is an user with the given username, 
compares the hashed passwords and sets a cookie with the token generated for the user an it's current session. 
Tokens are valid for 20 Minutes if there is noc action performed in this timeframe they will expire. 

If username or passwords are incorrect a Excetion will be thrown telling the user to check the credentials!</p>

<br>
<h3>Endpoints for registered users</h3>
<p>These Endpoints are only available to users, that have a user account and can submit a valid token</p>


<br><br>
<h2> Login and Authentication </h2>

<p>Under this section I will try to explain how the Authentication for Users and Admins is handled once it is implemented.</p>

<br>
<h3>Logging in and getting a token</h3>
<p>...</p>

<br>
<h3>Verifying actions with the token</h3>
<p>...</p>
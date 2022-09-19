## An Android Login App Demo

- What are used during development: 
  - [x] Kotlin
  - [x] MongoDB
  - [x] NodeJS
  - [x] Javascript
  - [x] Postman
  
 - How are the above being used within the structure of this Login app:
   - I start with creating a database with MongoDB to store user name, user email, hashed user password, along with the corresponding passwrod salt.
   - Then, with the help of NodeJS & Javascript, I try to create API services, namely login and register. 
   - Connect the database created previously to my localhost, and accordingly the 2 API calls could POST to the same localhost to interact with the database.
   - Use Postman to test the 2 API calls to see if they work as expected.
   - Update `activity_main.xml` to create UI.
   - Use Kotlin to implement API Service Interface and add onClick listeners in `MainActivity.kt`
  
 - Preview:
 ![alt text](https://raw.githubusercontent.com/lowdrag-zty/Android/main/screen.png)
 



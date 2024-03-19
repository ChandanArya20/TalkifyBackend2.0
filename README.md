# Talkify Backend - A Realtime Chat App

Welcome to the Takify backend service! This service powers the Takify real-time chat application, providing essential functionality and managing the database interactions.
It is developed using Spring Boot and serves as the backbone of Takify real-time chat application.
It provides a lot of API to handle HTTP request and also manage databse.

## Live Demo

    Frontend App Live Link: https://talkifychat.netlify.app/

## Table of Contents

- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [Contributing](#contributing)
- [Future Enhancements](#future-enhancements)
- [License](#license)

## Getting Started

### Prerequisites

Before you begin, ensure you have met the following requirements:

- Java Development Kit (JDK) 17 or higher installed.
- You can use Eclipse or intelliJ IDEA as IDE as per your preference
- Apache Maven installed for building and managing dependencies.
- A database server MySQL installed and configured.
  
  Note: You can use any SQL database but you have to make changes little bit in properties file of this project


### Installation

1. Clone the repository:

   ```shell
   git clone https://github.com/ChandanArya20/TalkifyBackend2.0.git
   ```
   
2. If you want you can refer 'Frontend Repository' as well:
  
   Github : https://github.com/ChandanArya20/talkify-frontend2.0.git
   
4. Now import this spring boot project in IDE and you are good to go
   
5. Use IDE to further build and run this project

## API Documentation
For API documentation, you can access the Swagger UI provided by the application. Here are the links:

Local Link : http://localhost:8080/swagger-ui.html

Live Link : http://booksbazaar.up.railway.app/swagger-ui.html

Note : You can change the host name in local link as per your requirement and need.

## Configuration

- Before running actual springboot project make sure You have replaced url and password of you DBMS in properties file of project according to enviroment

- This project includes different properties file for different enviroment so by default it will start in development enviroment but if you put 'ENV=prod' in application.properties file that time production enviroment will start to running

## Future Enhancements
- User Online/Offline Status
- Message read receipts (like sent, recieved or seen) 
- Audio/Video call facilities

## Contributing
If you'd like to contribute to the project, please follow the guidelines outlined in CONTRIBUTING.md.

## License
This project is licensed under the MIT License - feel free to use it for your own purposes.

Thank you for using our Takify backend service! If you have any questions or encounter any issues, please don't hesitate to contact us. You can find contact information in the developer section below.

Happy Chatting!

## Developer 
- **Chandan Kumar**
- **E-mail**: chandank1848@gmail.com
- **Linkedin**: www.linkedin.com/in/chandan2002
- **Github**: https://github.com/ChandanArya20

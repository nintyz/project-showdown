<template>
    <div class="landing-container">
        <div class="navbar">
            <img src="@/assets/logo.png" alt="Logo" class="logo" />
        </div>
        <div class="landing-body">
            <div class="d-flex justify-content-between">
                <div class="left-content d-flex align-items-center flex-column">
                    <h3 class="showdown-title">
                        <span v-for="(letter, index) in letters" :key="index" 
                              :style="{ animationDelay: `${index * 0.1}s` }" 
                              class="letter">{{ letter }}</span>
                    </h3>
                    <p class="showdown-description">
                        Showdown is a powerful tournament management system featuring a state-of-the-art matchmaking algorithm that ensures fair, competitive matchups.
                    </p>
                    <p @click="goToLogin" class="get-started">
                        Get Started
                        <font-awesome-icon :icon="['fas', 'arrow-right']" />
                    </p>
                </div>
                <div class="right-content">
                    <img src="@/assets/tennis-man.png" class="animated-image"/>
                </div>
            </div>
            <div class="d-flex tournament-content">
                <div class="tournament-header"> 
                    <h1 class="tournament-title"> TOURNAMENTS </h1>
                </div>
                <div class="tournament-body d-flex">
                    <div class="tournament-one">
                        <div class="tournament-image-header">
                            <img src="@/assets/tennis-tournament-one.jpg"/>
                        </div>
                        <h2 class="tournament-name">
                            Tournament 1
                        </h2>
                        <div class="tournament-location">
                            <font-awesome-icon :icon="['fas', 'location-dot']" class="icon-yellow" />
                            <span class="m-3">Acapulca, Mexico</span>
                        </div>
                    </div>
                    <div class="tournament-two">
                        <div class="tournament-image-header">
                            <img src="@/assets/tennis-tournament-two.jpg"/>
                        </div>
                        <h2 class="tournament-name">
                            Tournament 2
                        </h2>
                        <div class="tournament-location">
                            <font-awesome-icon :icon="['fas', 'location-dot']" class="icon-yellow" />
                            <span class="m-3">San Juan, Puerto Rico</span>
                        </div>
                    </div>
                    <div class="tournament-three">
                        <div class="tournament-image-header">
                            <img src="@/assets/tennis-tournament-three.jpg"/>
                        </div>
                        <h2 class="tournament-name">
                            Tournament 3
                        </h2>
                        <div class="tournament-location">
                            <font-awesome-icon :icon="['fas', 'location-dot']" class="icon-yellow" />
                            <span class="m-3">Miami, FL, USA</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
  </template>
  
  <script>
    import { library } from '@fortawesome/fontawesome-svg-core'
    import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
    import { faLocationDot, faArrowRight } from '@fortawesome/free-solid-svg-icons'
    
    library.add(faLocationDot, faArrowRight)


    export default {
        components: {
            FontAwesomeIcon,
        },
        data() {
            return {
                letters: 'SHOWDOWN'.split(''), // Split the title into an array of letters
            };
        },
        methods: {
            goToLogin() {
                this.$router.push('/login');
            },
        },
    };
  </script>
  
  <style scoped>
    .icon-yellow {
        color: #fea006;
    }

    .navbar {
        margin-left:100px;
    }
    
    .landing-container {
        background-color: #f3eeea;
        min-height: 100vh;
        overflow: hidden;
    }

    .logo {
        width: 250px;
    }

    .left-content {
        padding-top: 150px;
        padding-left: 250px;
        padding-right: 250px;
    }

    .showdown-title,
    .showdown-description {
        width: 100%;
        margin: 0 auto;
    }

    .showdown-title {
        color: #fea006;
        font-size: 100px;
        font-weight: 800;
        margin-bottom: 10px;
    }

    .letter {
        color: #fea006;
        opacity: 0;
        transform: translateY(30px); 
        animation: fadeInUp 1s forwards;
        letter-spacing: 3px;
    }

    @keyframes fadeInUp {
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .showdown-description {
        font-size: 20px;
        margin-top: 20px;
    }

    .get-started {
        color: #fea006;
        font-weight: 500;
        font-size: 20px;
        margin-top: 20px;
        align-self: flex-start;
        cursor: pointer;
    }

    .animated-image {
        transform: translateX(100%); 
        animation: slideIn 1s forwards;
    }

    @keyframes slideIn {
        to {
            transform: translateX(0);
        }
    }

    .tournament-content {
        height:100vh;
        background-color:white;
        flex-direction: column;
    }

    .tournament-header {
        margin-left: 120px;
        margin-top: 125px;
    }

    .tournament-body {
        justify-content: space-around;
        margin-top: 25px;
        padding-left: 80px;
        padding-right: 80px;
        align-items: center; /* Align all boxes to the same vertical level */
        text-align: left; /* Ensure the text inside the body is left-aligned */
    }

    .tournament-title {
        font-weight: 700;
        letter-spacing: 3px;
    }

    .tournament-one,
    .tournament-two,
    .tournament-three {
        background-color: #f3eeea;
        width: 500px;
        height: 70vh;
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        align-items: center;
        font-size: 24px;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); 
        margin: 5px;
    }

    .tournament-image-header {
        width: 100%;
        display: flex;
        justify-content: center;
    }

    .tournament-image-header img {
        width: 100%;
        height: 250px;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.5); 
    }

    .tournament-name {
        align-self: flex-start;
        text-align: left;
        font-weight: bold;
        margin-left: 20px;
        margin-top: 20px;
        font-size: 28px;
    }

    .tournament-location {
        align-self: flex-start;
        text-align: left;
        margin-left: 20px;
        margin-top: 10px;
        font-size: 18px;
    }
  </style>
  
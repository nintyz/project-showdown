<template>
    <div class="landing-container">
        <div class="navbar">
            <img src="@/assets/logo.png" alt="Logo" class="logo" />
        </div>
        <div class="d-flex justify-content-between landing-body">
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
    </div>
  </template>
  
  <script>
    import { library } from '@fortawesome/fontawesome-svg-core'
    import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
    import { faLocationDot, faArrowRight, faCalendar } from '@fortawesome/free-solid-svg-icons'
    
    library.add(faLocationDot, faArrowRight, faCalendar )


    export default {
        components: {
            FontAwesomeIcon,
        },
        data() {
            return {
                letters: 'SHOWDOWN'.split(''),
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
        margin-left:10%;
    }
    
    .landing-container {
        background-color: #f3eeea;
        min-height: 100vh;
        overflow: hidden;
    }

    .landing-body {
        height: 90vh;
    }

    .logo {
        width: 250px;
    }

    .left-content {
        width: 50%;
        padding-top: 10%;
        padding-left: 10%;
    }

    .right-content {
        display: flex;
        justify-content: flex-end;
        width: 50%;
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
        margin-top: 5%;
    }

    .get-started {
        color: #fea006;
        font-weight: 500;
        font-size: 20px;
        margin-top: 5%;
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

  </style>
  
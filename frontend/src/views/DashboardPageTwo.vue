<template>
    <div class="register-container">
        <!-- Navbar -->
        <Navbar />

        <!-- Tournament Details Section -->
        <div class="tournament-details container my-4 text-center">
            <div class="logo-wrapper">
                <img :src="tournament.logo" alt="Tournament Logo" class="tournament-logo" />
            </div>
            <h2 class="tournament-name">{{ tournament.name }}</h2>
            <p class="tournament-info">{{ tournament.venue }} | {{ tournament.date }}</p>
            <p class="registration-deadline"><strong>Registration Deadline:</strong> {{ tournament.registrationDeadline
                }}</p>
        </div>

        <div class="content container-fluid mx-5">
            <div class="row justify-content-center">
                <div class="col-lg-8 col-md-10">

                    <!-- Registered Players List -->
                    <div class="registered-players card p-4">
                        <h4>Registered Players</h4>
                        <ul class="list-group">
                            <li v-for="(player, index) in registeredPlayers" :key="index" class="list-group-item">
                                {{ player.name }}
                            </li>
                        </ul>
                    </div>

                    <!-- Bracket Info -->
                    <div class="bracket-info card mt-4 p-4">
                        <h5 class="text-danger">Bracket Information</h5>
                        <p>The tournament bracket is not available yet as registration is still open.</p>
                    </div>
                    <!-- Registration Section -->
                    <div class="registration-section card p-4 mt-2">
                        <h3 class="text-center">Register for the Tournament</h3>
                        <p class="text-muted text-center">Complete your registration before the deadline to participate
                            in the tournament.</p>
                        <button @click="registerForTournament" class="btn btn-primary w-100">Register Now</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import Navbar from '@/components/NavbarComponent.vue';
import '@/assets/main.css';

export default {
    components: {
        Navbar,
    },
    data() {
        return {
            tournament: {
                name: "Wimbledon 2024",
                venue: "London, UK",
                date: "1 - 14 December 2024",
                registrationDeadline: "10 November 2024",
                logo: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSg7iZb7c9r9kR4Quz_H0M9bgyeYyaPvJxJ7_hANon_A2V0EqjG1PmkuXowyF_ums6icjk&usqp=CAU",
            },
            registeredPlayers: [
                { name: "Alex" },
                { name: "John" },
                { name: "Nick" },
                { name: "Jake" },
            ],
        };
    },
    methods: {
        registerForTournament() {
            alert("You have successfully registered for Wimbledon 2024!");
        },
    },
};
</script>

<style scoped>
/* General Page Layout */
.register-container {
    background-color: #f3eeea;
    min-height: 100vh;
}

/* Tournament Details Section */
.tournament-details {
    background-color: #f8f4f0;
    border-radius: 10px;
    padding: 30px;
    margin-bottom: 30px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.logo-wrapper {
    margin-bottom: 20px;
}

.tournament-logo {
    width: 150px;
    height: auto;
}

.tournament-name {
    font-size: 28px;
    font-weight: 600;
    color: #776b5d;
}

.tournament-info,
.registration-deadline {
    font-size: 16px;
    color: #776b5d;
}

/* Registration Section */
.registration-section {
    background-color: #ffffff;
    border-radius: 8px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.btn-primary {
    background-color: #776b5d;
    border: none;
    border-radius: 5px;
    color: white;
    font-size: 16px;
    padding: 10px;
    margin-top: 15px;
    transition: background-color 0.3s ease;
}

.btn-primary:hover {
    background-color: #b0a695;
}

/* Registered Players Section */
.registered-players {
    background-color: #ffffff;
    border-radius: 8px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.list-group-item {
    background-color: #f3eeea;
    color: #776b5d;
    font-size: 16px;
    border: none;
    padding: 10px 15px;
    margin-bottom: 5px;
}

.list-group-item:last-child {
    margin-bottom: 0;
}

/* Bracket Info Section */
.bracket-info {
    background-color: #ffffff;
    border-radius: 8px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.bracket-info h5 {
    color: #dc3545;
    font-weight: 600;
}

.bracket-info p {
    font-size: 14px;
    color: #776b5d;
}
</style>
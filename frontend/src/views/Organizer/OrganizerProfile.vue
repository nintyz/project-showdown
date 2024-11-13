<template>
    <div class="profile-container">

        <Navbar />
        <div class="container-fluid p-5">
            <!-- Player Profile Header -->
            <div class="profile-header row align-items-center">
                <div class="player-image col-md-3 text-center">
                    <img src="@/assets/player-image.jpg" alt="Tennis Player" class="player-photo" />
                </div>
                <div class="player-info col-md-9">
                    <h1 class="player-name">{{ name }}</h1>
                    <h3>{{ organizerDetails.country }}</h3>
                    <p v-if="organizerDetails.bio" class="player-bio">
                        {{ organizerDetails.bio }}
                    </p>
                    <p v-else class="player-bio italic">
                        Organizer did not set any bio
                    </p>
                    <p class="player-bio">Verified on: 
                        {{ organizerDetails.dateVerified }}
                    </p>

                    <div class="action-buttons">
                        <button class="btn btn-secondary" @click="redirectToEditProfile">Update Profile</button>
                    </div>
                </div>
            </div>

            <!-- My Tournaments Section -->
            <div class="my-tournaments-section card mt-4">
                <h3 class="card-header">My Tournaments</h3>
                <ul class="tournament-list mt-4">
                    <li v-for="(tournament, index) in tournaments" :key="index"
                        class="row align-items-center mb-3 mx-2">
                        <div class="col-md-2">
                            <img :src="tournament.logo" alt="Tournament Logo" class="img-tournament" />
                        </div>
                        <div class="col-md-8">
                            <div class="tournament-info">
                                <h4>{{ tournament.name }}</h4>
                                <p>{{ tournament.venue }} | {{ formattedDateTime(tournament.dateTime) }}</p>
                            </div>
                        </div>
                        <div class="col-md-2 text-right">
                            <button class="btn btn-secondary" @click="viewTournament(tournament)">View</button>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </div>

</template>

<script>
import Navbar from '@/components/NavbarComponent.vue';
import axios from 'axios';
import '@/assets/main.css';

export default {
    components: {
        Navbar,
    },
    data() {
        return {
            organizerId:"",
            name:"",
            profileUrl:"",
            organizerDetails: {
                country: "",
                bio: "",
                dateVerified:"",
                websiteLink:""
            },
            tournaments: [],
        };
    },
    created() {
        this.fetchPlayerData();
        this.fetchTournaments();
    },
    computed: { 
        formattedDateTime() {
            return (dateTime) => {
                if (!dateTime) return '';
                return new Date(dateTime).toLocaleString(undefined, {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric',
                });
            };
        },
    },
    methods: {
        async fetchPlayerData() {
            this.organizerId = localStorage.getItem("userId")
            try {
                const response = await axios.get(`http://localhost:8080/user/${this.organizerId}`);
                this.organizerDetails = response.data.organizerDetails;
                this.name = response.data.name;
                this.profileUrl = response.data.profileUrl;

                // Create a Date object
                const date = new Date(response.data.organizerDetails.dateVerified);
                // Format to a more readable format
                const options = {
                year: 'numeric',
                month: 'long',    // 'long' gives the full month name; 'short' gives abbreviated
                day: 'numeric',
                hour: 'numeric',
                minute: 'numeric',
                second: 'numeric',
                hour12: true      // Use 12-hour format; set to false for 24-hour format
                };
                this.organizerDetails.dateVerified = date.toLocaleString('en-UK', options)
                    .replace(',', '')
                    .replace(':00', '');
            } catch (error) {
                console.error("Error fetching player data:", error);
            }
        },
        async fetchTournaments() {
            try {
                 const response = await axios.get(`http://localhost:8080/tournaments/organizer/${this.organizerId}`);
                 this.tournaments = response.data;
            } catch (error) {
                 console.error("Error fetching tournaments:", error);
            }
        },
        redirectToEditProfile() {
            this.$router.push('/edit-organizer');
        },
        viewTournament(tournament) {
            this.$router.push(`/tournament/${tournament.id}`);
        },
    },
};
</script>

<style scoped>
/* Main container styling */
.profile-container {
    background-color: #f3eeea;

    font-family: 'Arial', sans-serif;
}

.profile-header {
    background-color: #b0a695;
    color: white;
    padding: 30px;
    border-radius: 10px;
}

.player-photo {
    width: 200px;
    height: 200px;
    object-fit: cover;
    border-radius: 50%;
    border: 5px solid white;
}

.player-name {
    font-size: 36px;
    margin-bottom: 5px;
}

.player-bio {
    font-size: 16px;
    margin: 10px 0;
}

.action-buttons {
    margin-top: 10px;
}

/* Player stats */
.player-stats .stat label {
    font-weight: bold;
    font-size: 16px;
    color: #776b5d;
}

.player-stats .stat p {
    background-color: #fff;
    padding: 15px;
    border-radius: 8px;
    font-size: 18px;
    color: #776b5d;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

/* Achievements Section */
.achievements-section {
    background-color: #ebe3d5;
    /* padding: 20px; */
    border-radius: 10px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.achievements-section p {
    color: #776b5d;
}

.card-header {
    background: #776b5d;
    color: white;
}

.my-tournaments-section {
    background-color: #ebe3d5;
    /* padding: 20px; */
    border-radius: 10px;
    margin-top: 30px;
}

.tournament-list li {
    display: flex;
    align-items: center;
    background-color: #fff;
    padding: 15px;
    border-radius: 8px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.img-tournament {
    width: 100px;
    object-fit: cover;
    border-radius: 10px;
}

.tournament-info h4 {
    font-size: 18px;
    color: #776b5d;
}

.tournament-info p {
    font-size: 14px;
    color: #776b5d;
}
</style>

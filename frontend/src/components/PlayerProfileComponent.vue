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
                    <h1 class="player-name">{{ name }} ({{ calculateAge(player.dob) }} years)</h1>
                    <h3>{{ player.country }}</h3>
                    <h3>#{{ player.rank }}</h3>
                    <p v-if="player.bio" class="player-bio">
                        {{ player.bio }}
                    </p>
                    <p v-else class="player-bio italic">
                        Add your biography here.
                    </p>

                    <div class="action-buttons" v-if="isPersonalProfile">
                        <button class="btn btn-secondary" @click="redirectToEditProfile">Update Profile</button>
                    </div>
                </div>
            </div>

            <!-- Player Statistics -->
            <div class="player-stats row text-center mt-4">
                <div class="stat col-md-4">
                    <label>Date of Birth</label>
                    <p>{{ player.dob }}</p>
                </div>
                <div class="stat col-md-4">
                    <label>Rank</label>
                    <p>{{ player.rank }}</p>
                </div>
                <div class="stat col-md-4">
                    <label>ELO</label>
                    <p>{{ player.elo }}</p>
                </div>

            </div>

            <!-- Achievements Section -->
            <div class="achievements-section card mt-4">
                <h3 class="card-header">Achievements</h3>
                <div class="card-body">
                    <p v-if="player.achievements">{{ player.achievements }}</p>
                    <p v-else><em>No achievements added yet.</em></p>
                </div>
            </div>

            <!-- My Tournaments Section -->
            <div class="my-tournaments-section card mt-4">
                <h3 class="card-header">Tournaments</h3>
                <ul class="tournament-list mt-4">
                    <li v-for="(tournament, index) in tournaments" :key="index"
                        class="row align-items-center mb-3 mx-2">
                        <div class="col-md-2">
                            <img :src="tournament.logoUrl" alt="Tournament Logo" class="img-tournament" />
                        </div>
                        <div class="col-md-8">
                            <div class="tournament-info">
                                <h4>{{ tournament.name }}</h4>
                                <p>{{ tournament.country }} | {{ tournament.venue }}</p>
                            </div>
                        </div>
                        <div class="col-md-2 text-right">
                            <button class="btn btn-secondary" @click="viewTournament(tournament.id)">View</button>
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
    props: {
        userId: {
            type: String,
            default: null,
        },
    },
    data() {
        return {
            name: "",
            player: {
                dob: "",
                rank: "",
                elo: "",
                country: "",
                bio: "",
                achievements: "",
                clayRaw: "",
                grassRaw: "",
                hardRaw: "",
            },
            tournaments: [],
        };
    },
    computed: {
        isPersonalProfile() {
            return !this.userId;
        },
        computedUserId() {
            return this.userId || localStorage.getItem("userId");
        },
    },
    created() {
        this.fetchPlayerData();
        this.fetchTournaments();
    },
    methods: {
        async fetchPlayerData() {
            try {
                const response = await axios.get(`http://localhost:8080/user/${this.computedUserId}`);
                this.player = response.data.playerDetails;
                this.name = response.data.name;
            } catch (error) {
                console.error("Error fetching player data:", error);
            }
        },
        async fetchTournaments() {
            try {
                const response = await axios.get(`http://localhost:8080/tournaments/player/${this.computedUserId}`);
                this.tournaments = response.data;
            } catch (error) {
                console.error("Error fetching tournaments:", error);
            }
        },
        calculateAge(dob) {
            if (!dob) return '';
            const birthDate = new Date(dob);
            const today = new Date();
            let age = today.getFullYear() - birthDate.getFullYear();
            const monthDiff = today.getMonth() - birthDate.getMonth();
            if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
                age--;
            }
            return age;
        },
        redirectToEditProfile() {
            this.$router.push('/edit-profile');
        },
        viewTournament(id) {
            this.$router.push(`/tournament/${id}`);
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

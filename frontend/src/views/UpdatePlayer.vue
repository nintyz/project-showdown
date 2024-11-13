<template>
    <!-- Navbar -->
    <Navbar />
    <div class="edit-profile-container container-fluid pt-4">
        <h1>Edit Profile</h1>
        <form @submit.prevent="updateProfile">
            <div class="form-group">
                <label for="name">Name</label>
                <input type="text" id="name" v-model="name" class="form-control" />
            </div>
            <div class="form-group">
                <label for="dob">Date of Birth</label>
                <input type="date" id="dob" v-model="playerDetails.dob" class="form-control" />
            </div>
            <div class="form-group">
                <label for="country">Country</label>
                <input type="text" id="country" v-model="playerDetails.country" class="form-control" />
            </div>
            <div class="form-group">
                <label for="bio">Biography</label>
                <textarea id="bio" v-model="playerDetails.bio" class="form-control"></textarea>
            </div>
            <div class="form-group">
                <label for="achievements">Achievements</label>
                <textarea id="achievements" v-model="playerDetails.achievements" class="form-control"></textarea>
            </div>
            <button type="submit" class="btn btn-primary">Save Changes</button>
        </form>
    </div>
</template>

<script>
import Navbar from '@/components/NavbarComponent.vue';
import axios from 'axios';

export default {
    components: {
        Navbar,
    },
    data() {
        return {
            name: "",
            playerDetails: {
                dob: "",
                country: "",
                bio: "",
                achievements: "",
                clayRaw: "",
                grassRaw: "",
                hardRaw: "",
                elo: "",
                rank: "",
            }
        };
    },
    mounted() {
        this.fetchPlayerData();
    },
    methods: {
        async fetchPlayerData() {
            const userId = localStorage.getItem("userId");
            try {
                const response = await axios.get(`http://localhost:8080/user/${userId}`);
                this.name = response.data.name;
                this.playerDetails = {
                    ...this.playerDetails,
                    ...response.data.playerDetails
                };
            } catch (error) {
                console.error("Error fetching player data:", error);
            }
        },
        async updateProfile() {
            const userId = localStorage.getItem("userId");
            const token = localStorage.getItem("token");
            try {
                const updateData = {
                    name: this.name || undefined,
                    playerDetails: {
                        country: this.playerDetails.country || undefined,
                        bio: this.playerDetails.bio || undefined,
                        dob: this.playerDetails.dob || undefined,
                        achievements: this.playerDetails.achievements || undefined
                    }
                };

                Object.keys(updateData.playerDetails).forEach(key => 
                    updateData.playerDetails[key] === undefined && delete updateData.playerDetails[key]
                );

                await axios.put(
                    `http://localhost:8080/user/${userId}`, 
                    updateData, // Body
                    {
                        headers: {
                        Authorization: `Bearer ${token}`
                        }
                    } // Headers
                );
                // alert("Profile updated successfully");
                this.$router.push('/profile/player').then(() => {
                    window.location.reload();
                });
            } catch (error) {
                console.error("Error updating profile:", error);
                alert("Failed to update profile");
            }
        }
    },
};
</script>

<style scoped>
.edit-profile-container {
    background-color: #f3eeea;
    padding: 30px;
    font-family: 'Arial', sans-serif;
}

h1 {
    color: #776b5d;
    font-size: 36px;
    margin-bottom: 20px;
}

.form-group {
    margin-bottom: 20px;
}

.form-control {
    width: 100%;
    padding: 10px;
    font-size: 16px;
    border: 1px solid #b0a695;
    border-radius: 5px;
}

.btn-primary {
    background-color: #776b5d;
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 5px;
    font-size: 16px;
    cursor: pointer;
}

.btn-primary:hover {
    background-color: #b0a695;
}
</style>

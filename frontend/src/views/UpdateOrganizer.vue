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
                <label for="country">Country</label>
                <input type="text" id="country" v-model="organizerDetails.country" class="form-control" />
            </div>
            <div class="form-group">
                <label for="bio">Biography</label>
                <textarea id="bio" v-model="organizerDetails.bio" class="form-control"></textarea>
            </div>
            <div class="form-group">
                <label for="achievements">Website Link</label>
                <textarea id="achievements" v-model="organizerDetails.achievements" class="form-control"></textarea>
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
            name:"",
            profileUrl:"",
            organizerDetails: {
                country: "",
                bio: "",
                dateVerified:null,
                websiteLink:null
            },
        };
    },
    created() {
        this.fetchUserData();
    },
    methods: {
        async fetchUserData() {
            const userId = localStorage.getItem("userId");
            try {
                const response = await axios.get(`http://localhost:8080/user/${userId}`);
                this.name = response.data.name;
                this.profileUrl = response.data.profileUrl;
                this.organizerDetails = {
                    ...this.organizerDetails,
                    ...response.data.organizerDetails
                };
            } catch (error) {
                console.error("Error fetching user data:", error);
            }
        },
        async updateProfile() {
            const userId = localStorage.getItem("userId");
            const token = localStorage.getItem("token");
            try {
                const updateData = {
                    name: this.name || undefined,
                    organizerDetails: {
                        country: this.organizerDetails.country || undefined,
                        bio: this.organizerDetails.bio || undefined,
                        dateVerified: this.organizerDetails.dateVerified,
                        websiteLink: this.organizerDetails.websiteLink || undefined
                    }
                };

                // Remove undefined fields
                Object.keys(updateData.organizerDetails).forEach(key => 
                    updateData.organizerDetails[key] === undefined && delete updateData.organizerDetails[key]
                );

                // Only update the nested playerDetails object
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
                this.$router.push('/profile/personalOrganizer').then(() => {
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

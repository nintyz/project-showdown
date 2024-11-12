<template>
    <div class="organizer-details">
        <div class="form-group">
            <label for="fullname">Full Name</label>
            <input type="text" id="fullname" v-model="localOrganizerDetails.fullname"
                placeholder="Enter your full name" />
        </div>
        <div class="form-group">
            <label for="country">Country</label>
            <input type="text" id="country" v-model="localOrganizerDetails.country" placeholder="Enter your country" />
        </div>
        <div class="form-group">
            <label for="bio">Bio</label>
            <textarea id="bio" v-model="localOrganizerDetails.bio" placeholder="Enter a brief bio"></textarea>
        </div>
        <div class="form-group">
            <label for="website">Website</label>
            <input type="url" id="website" v-model="localOrganizerDetails.website"
                placeholder="Enter your website link" />
        </div>
    </div>
</template>

<script>
export default {
    props: ['organizerDetails'],
    data() {
        return {
            localOrganizerDetails: { ...this.organizerDetails },
        };
    },
    methods: {
        getFormData() {
            return this.localOrganizerDetails;
        }
    },
};
</script>

<style scoped>
.user-details-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #f3eeea;
  height: 100vh;
  justify-content: center;
  padding: 20px;
}

.logo {
  width: 150px;
  margin-bottom: 1em;
}

.details-box-wrapper {
  background-color: white;
  padding: 30px;
  max-width: 800px;
  width: 100%;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}

h1 {
  font-size: 24px;
  color: #333;
  text-align: center;
  margin-bottom: 20px;
}

.user-id-display {
  font-size: 14px;
  color: #666;
  text-align: center;
  margin-bottom: 15px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  column-gap: 20px;
  row-gap: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.full-width {
  grid-column: span 2;
}

label {
  font-weight: bold;
  margin-bottom: 5px;
}

input,
textarea {
  padding: 8px;
  border: 1px solid #b0a695;
  border-radius: 5px;
  background-color: #f3eeea;
}

input[disabled] {
  background-color: #e9ecef;
  cursor: not-allowed;
}

textarea {
  resize: vertical;
  min-height: 60px;
}

.save-btn {
  width: 100%;
  padding: 12px;
  background-color: #776b5d;
  border: none;
  border-radius: 5px;
  color: white;
  font-size: 16px;
  cursor: pointer;
  margin-top: 20px;
}

.save-btn:hover {
  background-color: #b0a695;
}

.form-group label input[type="checkbox"] {
  margin-right: 10px;
}
</style>
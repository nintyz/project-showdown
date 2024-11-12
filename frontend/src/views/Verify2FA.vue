<template>
  <div class="verify-container">
    <div class="verify-box">
      <img src="@/assets/logo.png" alt="Logo" class="logo">
      <h2>Two-Factor Authentication</h2>
      <p>Please enter the 6-digit code from your authenticator app</p>

      <div class="verification-form">
        <div class="code-input">
          <input type="text" v-model="twoFactorCode" placeholder="Enter 2FA code" maxlength="6" :disabled="isLoading"
            @keypress="numberOnly($event)" />
        </div>

        <button @click="verify2FA" :disabled="isLoading || !isValidCode" class="verify-btn">
          {{ isLoading ? 'Verifying...' : 'Verify' }}
        </button>

        <div v-if="error" class="error-message">{{ error }}</div>
        <div v-if="success" class="success-message">{{ success }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      email: this.$route.query.email,
      twoFactorCode: '',
      error: null,
      success: null,
      isLoading: false,
      role: localStorage.getItem("role"),
    };
  },
  computed: {
    isValidCode() {
      return this.twoFactorCode.length === 6 && /^\d+$/.test(this.twoFactorCode);
    }
  },
  methods: {
    async verify2FA() {
      this.isLoading = true;
      this.error = null;
      this.success = null;

      try {
        const response = await axios.post('http://localhost:8080/verify-2fa', {
          email: this.email,
          code: this.twoFactorCode
        });

        if (response.data.token) {
          const token = response.data.token;
          const role = this.extractRoleFromToken(token);

          localStorage.setItem('token', token);
          localStorage.setItem('role', role);

          this.success = 'Two-factor authentication successful!';
          this.redirectBasedOnRole();

          this.startRedirectCountdown(1.5);
          setTimeout(() => {
            this.$router.push('/dashboard');
          }, 1500);
        } else {
          this.error = 'Authentication failed. Please try again.';
        }
      } catch (error) {
        this.error = error.response?.data || 'Verification failed. Please try again.';
      } finally {
        this.isLoading = false;
      }
    },
    redirectBasedOnRole() {
      if (this.role === 'player') {
        this.$router.push('/user-details');
      } else {
        this.$router.push('/dashboard');
      }
    },
    numberOnly(event) {
      const charCode = event.which ? event.which : event.keyCode;
      if (charCode > 31 && (charCode < 48 || charCode > 57)) {
        event.preventDefault();
      }
    },
    extractRoleFromToken(token) {
      try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const payload = JSON.parse(window.atob(base64));
        return payload.role || 'player'; // Default to PLAYER if no role found
      } catch (error) {
        console.error('Error extracting role from token:', error);
        return 'player';
      }
    }
  }
};
</script>


<style scoped>
.verify-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f3eeea;
}

.verify-box {
  background-color: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  text-align: center;
}

.logo {
  width: 150px;
  margin-bottom: 1.5rem;
}

h2 {
  color: #776b5d;
  margin-bottom: 1rem;
}

.instruction {
  color: #666;
  margin-bottom: 1.5rem;
}

.success-message {
  color: #28a745;
  background-color: #d4edda;
  padding: 0.5rem;
  border-radius: 5px;
  margin-top: 1rem;
}

.redirect-countdown {
  font-size: 0.9rem;
  margin-top: 0.5rem;
  color: #666;
}

.code-input input:focus {
  outline: none;
  border-color: #776b5d;
  box-shadow: 0 0 0 2px rgba(119, 107, 93, 0.2);
}

.code-input input {
  width: 100%;
  padding: 0.8rem;
  margin-bottom: 1rem;
  border: 1px solid #b0a695;
  border-radius: 5px;
  font-size: 1rem;
  text-align: center;
  letter-spacing: 0.2rem;
}

.verify-btn {
  width: 100%;
  padding: 0.8rem;
  background-color: #776b5d;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s;
}

.verify-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.error-message {
  color: #dc3545;
  background-color: #f8d7da;
  padding: 0.5rem;
  border-radius: 5px;
  margin-top: 1rem;
}
</style>
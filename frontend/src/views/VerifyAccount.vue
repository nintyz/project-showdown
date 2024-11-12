<template>
  <div class="verify-container">
    <div class="verify-box">
      <img src="@/assets/logo.png" alt="Logo" class="logo">

      <h2>Verify Your Account</h2>
      <p class="instruction">
        We've sent a verification code to <strong>{{ email }}</strong>
      </p>

      <div class="verification-form">
        <div class="code-input">
          <input
              type="text"
              v-model="verificationCode"
              placeholder="Enter verification code"
              maxlength="6"
              :disabled="isLoading"
              @keypress="numberOnly($event)"
          />
        </div>

        <div class="resend-section">
          <p style="margin-bottom: 0">Didn't receive the code?</p>
          <button
              @click="resendCode"
              :disabled="isLoading || resendCooldown > 0"
              class="resend-btn"
          >
            {{ resendCooldown > 0 ? `Resend code in ${resendCooldown}s` : 'Resend code' }}
          </button>
        </div>

        <button
            @click="verifyAccount"
            :disabled="isLoading || !isValidCode"
            class="verify-btn"
        >
          {{ isLoading ? 'Verifying...' : 'Verify Account' }}
        </button>

        <!-- ... resend section ... -->

        <div v-if="error" class="error-message">
          {{ error }}
        </div>
        <div v-if="success" class="success-message">
          {{ success }}
          <div v-if="redirectCountdown > 0" class="redirect-countdown">
            Redirecting to dashboard...
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'VerifyAccount',
  data() {
    return {
      email: '',
      verificationCode: '',
      error: null,
      success: null,
      isLoading: false,
      resendCooldown: 0,
      cooldownInterval: null
    };
  },
  created() {
    this.email = this.$route.query.email;
    this.verificationCode = this.$route.query.code || '';
    if (!this.email) {
      this.$router.push('/login');
    }
  },
  beforeUnmount() {
    if (this.cooldownInterval) {
      clearInterval(this.cooldownInterval);
    }
  },
  computed: {
    isValidCode() {
      return this.verificationCode.length === 6 && /^\d+$/.test(this.verificationCode);
    }
  },
  methods: {
    numberOnly(event) {
      const charCode = (event.which) ? event.which : event.keyCode;
      if (charCode > 31 && (charCode < 48 || charCode > 57)) {
        event.preventDefault();
      }
    },
    startRedirectCountdown(seconds) {
      this.redirectCountdown = seconds;
      this.redirectInterval = setInterval(() => {
        if (this.redirectCountdown > 0) {
          this.redirectCountdown--;
        } else {
          clearInterval(this.redirectInterval);
        }
      }, 1000);
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
    },
    async verifyAccount() {
      this.isLoading = true;
      this.error = null;
      this.success = null;

      try {
        const response = await axios.post('http://localhost:8080/verify', {
          email: this.email,
          verificationCode: this.verificationCode
        });

        if (response.data === "Account is already verified") {
          this.success = "Account is already verified.";
          this.startRedirectCountdown(1.5);
          setTimeout(() => {
            this.$router.push('/dashboard');
          }, 1500);
          return;
        }

        this.success = response.data.message || 'Account verified successfully!';

        if (response.data.status === 'requires_2fa') {
          this.startRedirectCountdown(1.5);
          setTimeout(() => {
            this.$router.push(`/verify-2fa?email=${this.email}`);
          }, 1500);
        } else if (response.data.token) {
          const token = response.data.token;
          const role = "player"//this.extractRoleFromToken(token);

          localStorage.setItem('token', token);
          localStorage.setItem('role', role);

          this.startRedirectCountdown(1.5);
          setTimeout(() => {
            this.$router.push('/dashboard');
          }, 1500);
        }
      } catch (error) {
        if (error.response?.data === "Account is already verified") {
          this.success = "Account is already verified.";
          this.startRedirectCountdown(1.5);
          setTimeout(() => {
            this.$router.push('/dashboard');
          }, 1500);
        } else {
          this.error = error.response?.data || 'Verification failed. Please try again.';
        }
      } finally {
        this.isLoading = false;
      }
    },
    async resendCode() {
      this.isLoading = true;
      this.error = null;
      this.success = null;

      try {
        await axios.post('http://localhost:8080/resend', null, {
          params: {email: this.email}
        });

        this.success = 'New verification code sent!';
        this.startResendCooldown();
      } catch (error) {
        this.error = error.response?.data || 'Failed to resend code. Please try again.';
      } finally {
        this.isLoading = false;
      }
    },
    startResendCooldown() {
      this.resendCooldown = 60; // 60 seconds cooldown
      this.cooldownInterval = setInterval(() => {
        if (this.resendCooldown > 0) {
          this.resendCooldown--;
        } else {
          clearInterval(this.cooldownInterval);
        }
      }, 1000);
    }
  }
};
</script>

<style scoped>
.redirect-countdown {
  font-size: 0.9rem;
  margin-top: 0.5rem;
  color: #666;
}

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

.code-input input:focus {
  outline: none;
  border-color: #776b5d;
  box-shadow: 0 0 0 2px rgba(119, 107, 93, 0.2);
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

.resend-section {
  color: #666;
}

.resend-btn {
  background: none;
  border: none;
  color: #776b5d;
  text-decoration: underline;
  cursor: pointer;
  padding: 0.5rem;
}

.resend-btn:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.error-message {
  color: #dc3545;
  background-color: #f8d7da;
  padding: 0.5rem;
  border-radius: 5px;
  margin-top: 1rem;
}

.success-message {
  color: #28a745;
  background-color: #d4edda;
  padding: 0.5rem;
  border-radius: 5px;
  margin-top: 1rem;
}
</style>
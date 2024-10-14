<template>
  <div>
    <div v-if="isChatOpen" class="chatbot-window">
      <div class="chatbot-header">
        <span>ShowdownBot</span>
        <button class="close-btn" @click="toggleChat">
          <img src="@/assets/close.png" alt="Close" />
        </button>
      </div>

      <div class="chatbot-body" ref="chatBody">
        <div v-for="message in messages" :key="message.id" class="message-container">
          <!-- User Messages -->
          <div v-if="message.sender === 'user'" class="d-flex flex-row-reverse">
            <div class="user-message">
              <p style="margin-bottom: 0;">{{ message.text }}</p>
            </div>
          </div>
          <!-- Bot Messages -->
          <div v-if="message.sender === 'bot'" class="d-flex">
            <div class="bot-message">
              <p style="margin-bottom: 0;">{{ message.text }}</p>
            </div>
          </div>
        </div>
        <!-- Bot Typing -->
        <div v-if="isBotTyping" class="d-flex">
          <div class="bot-message typing-indicator">
            <div class="dot"></div>
            <div class="dot"></div>
            <div class="dot"></div>
          </div>
        </div>
      </div>

      <div class="chatbot-input">
        <textarea
          v-model="userInput"
          type="text"
          placeholder="Type a message..."
          @keyup.enter="sendMessage"
          rows="1"
          class="chatbot-textarea"
        ></textarea>
        <img 
          src="@/assets/send.png" 
          alt="Send" 
          class="send-btn" 
          @click="sendMessage"
          :class="{ 'disabled': !userInput.trim() }"
          :style="{ opacity: userInput.trim() ? 1 : 0.5 }"
          :disabled="!userInput.trim()"
        />
      </div>
    </div>

    <button v-if="!isChatOpen" @click="toggleChat" class="chatbot-toggle-btn">
      <img src="@/assets/chatbot.png" alt="Chatbot Icon" />
    </button>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      isChatOpen: false,
      chatOpened: false,
      userInput: '',
      messages: [],
      isBotTyping: false,
    };
  },
  methods: {
    toggleChat() {
      this.isChatOpen = !this.isChatOpen;

      if (this.chatOpened == false) {
        this.fetchWelcomeMessage();
        this.chatOpened = true;
      }
    },
    async fetchWelcomeMessage() {
      this.isBotTyping = true;

      try {
        const response = await axios.post('http://localhost:8080/chatbot/message', {
          message: 'hi',
        });

        const botResponse = response.data.queryResult.fulfillmentText;

        const botMessage = {
          id: Date.now() + 1,
          text: botResponse,
          sender: 'bot',
        };
        this.messages.push(botMessage);
        this.scrollToBottom();
      } catch (error) {
        console.error('Error fetching welcome message:', error);
        const errorMessage = {
          id: Date.now() + 2,
          text: "Sorry, I couldn't get a response. Please try again later.",
          sender: 'bot',
        };
        this.messages.push(errorMessage);
        this.scrollToBottom();
      } finally {
        this.isBotTyping = false;
      }
    },
    async sendMessage() {
      if (this.userInput.trim() !== '') {
        const userMessage = {
          id: Date.now(),
          text: this.userInput,
          sender: 'user',
        };
        this.messages.push(userMessage);

        const inputMessage = this.userInput;
        this.userInput = '';

        this.scrollToBottom();
        this.isBotTyping = true;

        try {
          const botMessage = await this.sendQueryWithRetry(inputMessage);
          this.messages.push(botMessage);
        } catch (error) {
          console.error('Error sending message:', error);
          const errorMessage = {
            id: Date.now() + 2,
            text: "Sorry, I couldn't get a response. Please try again later.",
            sender: 'bot',
          };
          this.messages.push(errorMessage);
        } finally {
          this.scrollToBottom();
          this.isBotTyping = false;
        }
      }
    },
    async sendQueryWithRetry(inputMessage, retries = 3, delay = 500) {
      let attempt = 0;
      while (attempt < retries) {
        try {
          const response = await axios.post('http://localhost:8080/chatbot/message', {
            message: inputMessage,
          });

          const fulfillmentText = response.data.queryResult?.fulfillmentText;

          if (fulfillmentText) {
            return {
              id: Date.now() + 1,
              text: fulfillmentText,
              sender: 'bot',
            };
          }

          attempt++;
          if (attempt < retries) {
            await this.delay(delay);
          }
        } catch (error) {
          console.error('Dialogflow API error:', error);
          throw error;
        }
      }

      return {
        id: Date.now() + 2,
        text: "I'm having trouble understanding. Please try again.",
        sender: 'bot',
      };
    },
    delay(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const chatBody = this.$refs.chatBody;
        chatBody.scrollTop = chatBody.scrollHeight;
      });
    },
  },
};
</script>

<style scoped>
.chatbot-window {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 300px;
  height: 400px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  z-index: 1000;
  display: flex;
  flex-direction: column;
}

.chatbot-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #007bff;
  color: white;
  padding: 10px;
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
}

.chatbot-body {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  max-height: 300px;
}

.chatbot-body::-webkit-scrollbar {
  display: none;
}

.user-message,
.bot-message {
  margin: 5px 0;
  padding: 10px;
  border-radius: 10px;
  max-width: 70%;
  word-wrap: break-word;
}

.user-message {
  background-color: #007bff;
  color: white;
  align-self: flex-end;
}

.bot-message {
  background-color: #f1f1f1;
  align-self: flex-start;
}

.chatbot-input {
  display: flex;
  padding: 10px;
  border-top: 1px solid #ccc;
}

.typing-indicator {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.dot {
  width: 6px;
  height: 6px;
  margin: 0 2px;
  background-color: #333;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out both;
}

.dot:nth-child(1) {
  animation-delay: -0.32s;
}
.dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes typing {
  0%,
  80%,
  100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}


.chatbot-textarea {
  flex: 1;
  margin-right: 8px;
  border-radius: 4px;
  border: none;
  resize: none;
}

.chatbot-textarea:focus {
  outline: none;
}

.send-btn {
  width: 30px;
  height: 30px;
  margin: auto;
  cursor: pointer;
}

.send-btn.disabled {
  cursor: default;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
}

.close-btn img {
  width: 12px;
  height: 12px;
}

.chatbot-toggle-btn {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 50%;
  padding: 15px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  cursor: pointer;
}

.chatbot-toggle-btn img {
  width: 30px;
  height: 30px;
}

.chatbot-toggle-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
}
</style>

// js/main.js
const { createApp } = Vue;

// Axios is already global as window.axios
const apiClient = axios.create({
    baseURL: '/api',
    headers: { 'Content-Type': 'application/json' }
});

// Root component
const App = {
    template: `
      <v-app>
        <v-main>
          <v-container class="mt-5">
            <h1>Sudoku</h1>

            <v-select
                v-model="selectedDifficulty"
                :items="difficulties"
                item-title="label"
                item-value="id"
                label="Select Difficulty"
                outlined
            ></v-select>

            <v-btn color="primary" class="mt-3" @click="generateBoard" :disabled="!selectedDifficulty">
              Generate Board
            </v-btn>

            <div class="sudoku-grid mt-5" v-if="board.length">
              <v-row v-for="(row, rowIndex) in board" :key="rowIndex">
                <v-col v-for="(cell, colIndex) in row" :key="colIndex" cols="1">
                  <v-text-field v-model="board[rowIndex][colIndex]" maxlength="1" outlined class="sudoku-cell"></v-text-field>
                </v-col>
              </v-row>
            </div>
          </v-container>
        </v-main>
      </v-app>
    `,
    data() {
        return {
            difficulties: [],
            selectedDifficulty: null,
            board: []
        };
    },
    mounted() {
        this.fetchDifficulties();
    },
    methods: {
        async fetchDifficulties() {
            try {
                const res = await apiClient.get('/sudoku_difficulty/get_all');
                this.difficulties = res.data;
            } catch (err) {
                console.error(err);
            }
        },
        async generateBoard() {
            try {
                const res = await apiClient.get(`/sudoku/generate/${this.selectedDifficulty}`);
                this.board = res.data;
            } catch (err) {
                console.error(err);
            }
        }
    }
};

// Vuetify 3 CDN
const vuetify = Vuetify.createVuetify();

// Mount Vue
createApp(App).use(vuetify).mount('#app');

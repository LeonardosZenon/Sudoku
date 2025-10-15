const { createApp, ref, onMounted } = Vue;
const { createVuetify } = Vuetify;

const App = {
    setup() {
        const title = ref("SUDOKU Engine");
        const drawer = ref(false);
        const difficulties = ref([]);   // reactive empty array
        const board = ref(null);

        // Fetch difficulties when component mounts
        onMounted(async () => {
            try {
                const response = await axios.get('/api/sudoku_difficulties/get_all');
                difficulties.value = response.data; // assign API result
            } catch (error) {
                console.error("Failed to load difficulties:", error);
            }
        });

        // Methods defined inline for clarity
        const logout = () => {
            window.location.href = "/api/user/logout";
        };

        const generate = async (diff) => {
            drawer.value = false;
            try {
                const response = await axios.get(`/api/sudoku/generate/${diff}`);
                board.value = response.data;
            } catch (err) {
                console.error("Failed to generate Sudoku:", err);
            }
        };

        return { title, drawer, difficulties, board, logout, generate };
    },

    template: `
    <v-app>
      <v-app-bar color="primary" dark>
        <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
        <v-toolbar-title>{{ title }}</v-toolbar-title>
        <v-spacer/>
        <v-btn color="error" variant="tonal" @click="logout">Logout</v-btn>
      </v-app-bar>

      <v-navigation-drawer v-model="drawer" temporary>
        <v-list>
          <v-list-item
            v-for="d in difficulties"
            :key="d"
            @click="generate(d)"
          >
            {{ d }}
          </v-list-item>
        </v-list>
      </v-navigation-drawer>

      <v-main>
        <v-container class="text-center">
          <h2 class="mt-10">Sudoku:</h2>
          <div v-if="!board">Select a difficulty to generate a Sudoku.</div>
          <div v-else>🧩 Sudoku board will appear here.</div>
        </v-container>
      </v-main>
    </v-app>
  `
};

const vuetify = createVuetify();
createApp(App).use(vuetify).mount("#app");

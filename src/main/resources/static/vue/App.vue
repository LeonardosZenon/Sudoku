<v-app>
<v-app-bar color="primary" dark>
  <v-app-bar-nav-icon></v-app-bar-nav-icon>
  <v-toolbar-title>
    {{ title }}
  </v-toolbar-title>
  <v-spacer/>
  <v-btn color="error" variant="tonal" @click="logout">logout</v-btn>
</v-app-bar>
<v-main>
  <v-container class="text-center">
    <h2 class="mt-10">Sudoku:</h2>
  </v-container>
</v-main>
</v-app>

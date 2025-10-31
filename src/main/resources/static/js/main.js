const SudokuGame = {
    template: `
        <v-container>
            <v-row justify="center">
                <v-col cols="12" sm="8" md="6">
                    <v-card>
                        <v-card-title class="text-center">Sudoku</v-card-title>
                        
                        <v-card-text v-if="!board">
                            <v-select
                                v-model="difficulty"
                                :items="[1,2,3,4]"
                                label="Difficulty"
                            ></v-select>
                            <v-btn block color="primary" @click="generateBoard">New Game</v-btn>
                            <v-btn v-if="hasInProgress" block color="secondary" class="mt-2" @click="continueGame">
                                Continue Game
                            </v-btn>
                        </v-card-text>

                        <v-card-text v-else>
                            <v-alert type="success" dense :text="true" v-if="board && board.solved">Solved! Well done!</v-alert>

                            <div class="sudoku-board">
                                <!-- iterate the actual playable array returned by the backend -->
                                <div v-for="(row, i) in playable" :key="'row-'+i" class="sudoku-row">
                                    <div v-for="(val, j) in row" :key="'cell-'+i+'-'+j"
                                         class="sudoku-cell"
                                         :class="{'initial': val !== 0}">
                                        <v-text-field
                                            :value="(board && board.gridCurrentArray && board.gridCurrentArray[i]) ? (board.gridCurrentArray[i][j] === 0 ? '' : String(board.gridCurrentArray[i][j])) : ''"
                                            :readonly="val !== 0"
                                            :rules="[v => !v || (/^[0-9]$/.test(v)) || 'Invalid']"
                                            @input="onCellRawInput(i, j, $event)"
                                            @keydown.enter.native.prevent="onEnter(i, j)"
                                            @keydown.native="onKeyDown(i, j, $event)"
                                            @paste.native.prevent="onPaste(i, j, $event)"
                                            maxlength="1"
                                            inputmode="numeric"
                                            pattern="[0-9]*"
                                            type="tel"
                                            single-line
                                            dense
                                            hide-details
                                        ></v-text-field>
                                     </div>
                                 </div>
                            </div>

                            <div class="mt-4">
                                <template v-if="board && (board.solved || isComplete)">
                                    <v-btn block color="primary" class="mb-2" @click="playAgain">New Game</v-btn>
                                </template>
                                <!-- no manual Validate button: validation happens automatically on input/paste/keypress -->
                            </div>

                        </v-card-text>
                    </v-card>
                </v-col>
            </v-row>
        </v-container>
    `,
    data: () => ({
        difficulty: 1,
        board: null,
        hasInProgress: false,
        lastMove: null
    }),

    async created() {
        try {
            const response = await axios.get('/api/sudoku/isinprogress');
            this.hasInProgress = response.data;
        } catch (error) {
            console.error('Error checking game progress:', error);
        }
    },

    computed: {
        playable() {
            if (this.board && Array.isArray(this.board.gridPlayableArray)) return this.board.gridPlayableArray;
            // default empty 9x9
            return Array.from({ length: 9 }, () => Array(9).fill(0));
        },
        isComplete() {
            if (!this.board || !Array.isArray(this.board.gridCurrentArray)) return false;
            for (let r = 0; r < 9; r++) {
                for (let c = 0; c < 9; c++) {
                    if (!this.board.gridCurrentArray[r] || this.board.gridCurrentArray[r][c] === 0) return false;
                }
            }
            return true;
        },
         // `current` removed because we bind directly to board.gridCurrentArray
    },

    methods: {
        // Ensure the board has proper 9x9 numeric arrays for playable and current grids
        normalizeBoard(raw) {
            if (!raw) return null;
            const board = Object.assign({}, raw);

            function strToGridArray(str) {
                if (!str || typeof str !== 'string') return null;
                const cleaned = str.trim();
                if (cleaned.length !== 81) return null;
                const arr = [];
                for (let r = 0; r < 9; r++) {
                    const row = [];
                    for (let c = 0; c < 9; c++) {
                        const ch = cleaned[r * 9 + c];
                        const n = parseInt(ch, 10);
                        row.push(Number.isNaN(n) ? 0 : n);
                    }
                    arr.push(row);
                }
                return arr;
            }

            // gridPlayableArray: may be already an array or could be present as string gridPlayable
            if (!Array.isArray(board.gridPlayableArray)) {
                const candidate = board.gridPlayable || board.gridPlayableArray || board.gridPlayableString;
                const parsed = strToGridArray(candidate);
                if (parsed) board.gridPlayableArray = parsed;
            }

            // gridCurrentArray: may be array or string gridCurrent
            if (!Array.isArray(board.gridCurrentArray)) {
                const candidate = board.gridCurrent || board.gridCurrentArray || board.gridCurrentString;
                const parsed = strToGridArray(candidate);
                if (parsed) board.gridCurrentArray = parsed;
            }

            // If only gridCurrentArray exists and gridPlayableArray is missing, derive playable from current (non-zero are initial)
            if (!Array.isArray(board.gridPlayableArray) && Array.isArray(board.gridCurrentArray)) {
                board.gridPlayableArray = board.gridCurrentArray.map(row => row.map(v => (v ? v : 0)));
            }

            // If gridCurrentArray is missing but playable exists, initialize current as a copy
            if (!Array.isArray(board.gridCurrentArray) && Array.isArray(board.gridPlayableArray)) {
                board.gridCurrentArray = board.gridPlayableArray.map(row => row.slice());
            }

            // Ensure both arrays are 9x9, sanitize values and coerce numbers
            function ensure9x9(arr) {
                const out = [];
                for (let r = 0; r < 9; r++) {
                    const srcRow = Array.isArray(arr) && Array.isArray(arr[r]) ? arr[r] : [];
                    const row = [];
                    for (let c = 0; c < 9; c++) {
                        const v = srcRow[c];
                        const n = parseInt(v, 10);
                        row.push(Number.isNaN(n) ? 0 : Math.max(0, Math.min(9, n)));
                    }
                    out.push(row);
                }
                return out;
            }

            board.gridPlayableArray = ensure9x9(board.gridPlayableArray);
            board.gridCurrentArray = ensure9x9(board.gridCurrentArray);

            // ensure solved flag exists
            if (typeof board.solved === 'undefined') board.solved = false;
            // If backend returns a status, derive solved from it as well (accept id or label)
            if (board.status) {
                try {
                    const st = board.status;
                    const id = st.id;
                    const label = st.label && typeof st.label === 'string' ? st.label.toUpperCase() : null;
                    if (id === 1 || label === 'SOLVED') {
                        board.solved = true;
                    }
                } catch (e) {
                    // ignore and keep existing board.solved
                }
            }

             return board;
         },

        async generateBoard() {
            try {
                const response = await axios.get(`/api/sudoku/generate/${this.difficulty}`);
                this.board = this.normalizeBoard(response.data);
                this.lastMove = null;
            } catch (error) {
                console.error('Error generating board:', error);
            }
        },

        async continueGame() {
            try {
                const response = await axios.get('/api/sudoku/continue');
                this.board = this.normalizeBoard(response.data);
                this.lastMove = null;
            } catch (error) {
                console.error('Error continuing game:', error);
            }
        },

        async validateBoard() {
            try {
                // Backend expects a move inside the DTO. We capture the last edited cell as lastMove.
                if (!this.lastMove || typeof this.lastMove.value === 'undefined' || this.lastMove.value === 0) {
                    alert('Make a move (enter a number into any editable cell) before validating.');
                    return;
                }

                const payload = {
                    id: this.board ? this.board.id : null,
                    move: {
                        value: this.lastMove.value,
                        position: { x: this.lastMove.x, y: this.lastMove.y }
                    }
                };

                const response = await axios.post('/api/sudoku/validate', payload);
                this.board = this.normalizeBoard(response.data);
                if (this.board && this.board.solved) {
                    alert('Congratulations! Puzzle solved!');
                }
                // after a successful validation, clear lastMove (so next validate requires another edit)
                this.lastMove = null;
             } catch (error) {
                 console.error('Error validating board:', error);
             }
        },

        playAgain() {
            // return to the start screen so the user can pick difficulty again
            this.lastMove = null;
            this.board = null;
        },

        // New handler: receives raw string from v-text-field, keeps only a single digit (last typed) and updates the numeric grid
        onCellRawInput(i, j, raw) {
            const str = raw == null ? '' : String(raw);
            // find all digits in the input and take the last one (most recent)
            const matches = str.match(/\d/g);
            const ch = matches && matches.length ? matches[matches.length - 1] : '';
            const v = ch === '' ? 0 : Math.max(0, Math.min(9, parseInt(ch, 10)));

            if (!this.board || !this.board.gridCurrentArray || !this.board.gridCurrentArray[i]) return;
            this.$set(this.board.gridCurrentArray[i], j, v);

            if (v > 0) {
                this.lastMove = { x: i, y: j, value: v };
                // auto-validate after a user input move, unless board already solved
                if (!this.board || !this.board.solved) {
                    this.validateBoard();
                }
            } else {
                if (this.lastMove && this.lastMove.x === i && this.lastMove.y === j) {
                    this.lastMove = null;
                }
            }
        },

        onEnter(i, j) {
            // wait one tick so v-model update from the input is applied, then read the value and validate
            this.$nextTick(() => {
                if (!this.board || !this.board.gridCurrentArray || !Array.isArray(this.board.gridCurrentArray[i])) return;
                const value = parseInt(this.board.gridCurrentArray[i][j], 10);
                const v = Number.isNaN(value) ? 0 : Math.max(0, Math.min(9, value));
                if (v > 0) this.lastMove = { x: i, y: j, value: v };
                this.validateBoard();
            });
        },

        onKeyDown(i, j, event) {
            // intercept key presses to enforce single-digit behavior
            const k = event.key;
            if (/^[0-9]$/.test(k)) {
                const v = parseInt(k, 10);
                if (!this.board || !this.board.gridCurrentArray || !this.board.gridCurrentArray[i]) return event.preventDefault();
                this.$set(this.board.gridCurrentArray[i], j, v);
                this.lastMove = { x: i, y: j, value: v };
                // prevent default insertion (we've already set the value)
                event.preventDefault();
                // auto-validate after a key press
                if (!this.board || !this.board.solved) this.validateBoard();
                return;
            }
            if (k === 'Backspace' || k === 'Delete') {
                if (!this.board || !this.board.gridCurrentArray || !this.board.gridCurrentArray[i]) return event.preventDefault();
                this.$set(this.board.gridCurrentArray[i], j, 0);
                if (this.lastMove && this.lastMove.x === i && this.lastMove.y === j) this.lastMove = null;
                event.preventDefault();
                return;
            }
            // allow arrow keys, tab, enter (enter handled separately), otherwise prevent
            if (["ArrowLeft","ArrowRight","ArrowUp","ArrowDown","Tab","Enter"].includes(k)) return;
            event.preventDefault();
        },

        onPaste(i, j, event) {
            const text = (event.clipboardData || window.clipboardData).getData('text') || '';
            const matches = text.match(/\d/g);
            const ch = matches && matches.length ? matches[matches.length - 1] : '';
            const v = ch === '' ? 0 : Math.max(0, Math.min(9, parseInt(ch, 10)));
            if (!this.board || !this.board.gridCurrentArray || !this.board.gridCurrentArray[i]) {
                event.preventDefault();
                return;
            }
            this.$set(this.board.gridCurrentArray[i], j, v);
            if (v > 0) {
                this.lastMove = { x: i, y: j, value: v };
                if (!this.board || !this.board.solved) this.validateBoard();
            } else if (this.lastMove && this.lastMove.x === i && this.lastMove.y === j) {
                this.lastMove = null;
            }
             event.preventDefault();
         }
    }
};

new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    components: {
        'sudoku-game': SudokuGame
    }
});

module SPI_mnrch(clk, rst_n, SS_n, SCLK, MOSI, MISO, wrt, wt_data, done, rd_data);

  input clk, rst_n;
  input reg MISO;
  input wrt;
  input [15:0] wt_data;
  output reg SS_n, SCLK;
  output done, MOSI;
  output [15:0] rd_data;

  localparam rst_value = 4'b1011;

  reg shift, init, ld_SCLK, done16, set_done, idle;
  reg [3:0] SCLK_div;
  reg [4:0] bit_cntr;
  reg done;
  reg [15:0] shft_reg;

  // state definitions
  typedef enum reg [1:0] {IDLE,TRNSMTTNG,TRDWN} state_t;

  reg [1:0] curr_state, nxt_state;

  // divide by 16 clk logic
  always @(posedge clk)
    if (ld_SCLK)
      SCLK_div <= rst_value;
    else
      SCLK_div <= SCLK_div + 1; // overflow will naturally reset this to 0

  assign shift = (SCLK_div == 4'b1010) ? 1'b1 : 1'b0; // shift 2 clocks AFTER rise of SCLK

  // bit counter flop and logic
  always @(posedge clk)
    if (init)
      bit_cntr <= 5'b00000;
    else if (shift)
      bit_cntr <= bit_cntr + 1;

  assign done16 = bit_cntr[4]; // take MSB of bit_cntr instead of a reduction for off by one errors

  // shift out/in register logic
  always @(posedge clk)
    if (init)
      shft_reg <= wt_data;
    else if (shift)
      shft_reg <= {shft_reg[14:0], MISO}; // shift in the MISO line into the LSB

  assign MOSI = shft_reg[15]; // get the MOSI bit from the shift reg
  assign rd_data = shft_reg;

  // state machine transition block
  always_comb begin
    init = 1'b0;
    idle = 1'b0;
    ld_SCLK = 1'b0;
    nxt_state = 2'b00;
    set_done = 1'b0;
    casex (curr_state)
      IDLE: begin
        idle = 1'b1;
        ld_SCLK = 1'b1; // fixed - always assert ld_SCLK in IDLE
        if (wrt) begin
          nxt_state = TRNSMTTNG;
          init = 1'b1;
        end else
          nxt_state = IDLE;
      end
      TRNSMTTNG: begin
        if (done16) begin
          nxt_state = TRDWN;
        end else
          nxt_state = TRNSMTTNG;
      end
      default: begin // tear down state
        if (&SCLK_div[3:1]) begin // create 'backporch' of 6 extra cycles
                            // not 7 beauase it would change on the next clk and might glitch
          nxt_state = IDLE;
          set_done = 1'b1;
        end else
          nxt_state = TRDWN;
      end
    endcase
  end

  // SS_n and SCLK determination
  always @(posedge clk, negedge rst_n)
    if (!rst_n)
      SS_n <= 1'b1;
    else if (idle)
      SS_n <= 1'b1;
    else
      SS_n <= 1'b0;

  always @(posedge clk, negedge rst_n)
    if (!rst_n)
      SCLK <= 1'b1;
    else if (idle)
      SCLK <= 1'b1;
    else if (SCLK_div[3])
      SCLK <= 1'b1;
    else
      SCLK <= 1'b0;

  // current state logic
  always @(posedge clk, negedge rst_n)
    if (!rst_n)
      curr_state <= IDLE;
    else
      curr_state <= nxt_state;

  // SR flop for done variable
  always @(posedge clk, negedge rst_n)
    if (!rst_n)
      done <= 1'b0;
    else if (init)
      done <= 1'b0;
    else if (set_done)
      done <= 1'b1;

endmodule
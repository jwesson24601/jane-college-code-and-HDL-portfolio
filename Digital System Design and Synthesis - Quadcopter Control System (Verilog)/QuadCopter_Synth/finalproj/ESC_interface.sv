module ESC_interface(clk, rst_n, wrt, SPEED, PWM);
				
  // Author: Jordan Wilkins

  input clk, rst_n, wrt;
  input [10:0] SPEED;
  output reg PWM;

  wire all_zeros;
  reg [10:0] flop_speed;
  wire [11:0] double_speed;
  wire [12:0] triple_speed;
  wire [13:0] setting, setting_down;
  reg [13:0] hold_high;
  localparam MIN_SPEED = 6250;
  
  always @(posedge clk, negedge rst_n) begin
    if (!rst_n)
      flop_speed <= 11'h000;
    else
      flop_speed <= SPEED;
    end

  assign double_speed = {flop_speed[10:0], 1'b0}; // shift by 1 instead of using mult, easier to visualize synthesis
  assign triple_speed = double_speed + flop_speed; // add 2*speed + speed = 3*speed
  assign setting = MIN_SPEED + triple_speed;

  // COUNT DOWN FF
  always @(posedge clk, negedge rst_n) begin
    if (!rst_n)
      hold_high <= 14'h0000;
    else if (wrt) // write to the flop
      hold_high <= setting;
    else
      hold_high <= setting_down;
  end
  
  assign setting_down = hold_high - 1;
  assign all_zeros = ~(|hold_high);

  // SR FF implementation
  always @(posedge clk, negedge rst_n) begin
    if (!rst_n)
      PWM <= 1'b0;
    else if (wrt) // wrt = SET
      PWM <= 1'b1;
    else if (all_zeros) // all_zeros = RESET
      PWM <= 1'b0;
  end

endmodule 

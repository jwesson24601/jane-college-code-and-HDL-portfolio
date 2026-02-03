module UART_rcv(clk, rst_n, RX, clr_rdy, rx_data, rdy);

input clk, rst_n, clr_rdy, RX;
output reg [7:0] rx_data;
output reg rdy;

localparam baud_rate = 2604;
localparam baud_rate_half = 1302;

reg [8:0] shift_reg; // MSB will contain stop bit at rdy
reg [11:0] baud_cnt;
reg [3:0] bit_cnt;
reg rx_flop, rx_flop2; // double flops for meta stability

// outputs of SM
reg load, set_rdy, receive;

reg curr_state, nxt_state;
wire shift;

typedef enum reg {init, receiving} state_t;

// bit_cnt counter
always @(posedge clk, negedge rst_n) begin
    if (!rst_n)
        bit_cnt <= 4'b0000;
    else if (load)
        bit_cnt <= 4'b0000;
    else if (shift)
        bit_cnt <= bit_cnt + 1;
end

// baud_cnt counter
always @(posedge clk, negedge rst_n) begin
    if (!rst_n)
        baud_cnt <= baud_rate_half;
    else if (load)
        baud_cnt <= baud_rate_half;
    else if (shift)
        baud_cnt <= baud_rate;
    else if (receive)
        baud_cnt <= baud_cnt - 1;
end

// RX double flop
always @(posedge clk, negedge rst_n) begin
    if (!rst_n) begin // reset high since UART is idle high
        rx_flop <= 1'b1;
        rx_flop2 <= 1'b1;
    end else begin
        rx_flop <= RX;
        rx_flop2 <= rx_flop;
    end
end

// rx_data shift in
always @(posedge clk)
    if (shift)
        shift_reg <= {rx_flop2, shift_reg[8:1]};

assign shift = (baud_cnt == 12'h000) ? 1'b1 : 1'b0;
assign rx_data = shift_reg[7:0];

// state transition block
always_comb begin
    load = 1'b0;
    set_rdy = 1'b0;
    receive = 1'b0;
    nxt_state = init;
    case (curr_state)
        init: begin
            if (!rx_flop2) begin
                nxt_state = receiving;
                load = 1'b1;
            end else 
                nxt_state = init;
            end
        default: begin // receiving state
            if (bit_cnt == 4'hA) begin
                set_rdy = 1;
                nxt_state = init;
            end else
                nxt_state = receiving;
            receive = 1;
        end
    endcase
end

// curr_state flop
always @(posedge clk, negedge rst_n)
    if (!rst_n)
        curr_state <= init;
    else
        curr_state <= nxt_state;

// rdy flop
always @(posedge clk, negedge rst_n)
    if (!rst_n)
        rdy <= 1'b0;
    else if (load || clr_rdy)
        rdy <= 1'b0;
    else if (set_rdy)
        rdy <= 1'b1;

endmodule
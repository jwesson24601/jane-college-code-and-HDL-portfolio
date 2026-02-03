module RemoteComm(clk, rst_n, RX, TX, cmd, data, send_cmd, cmd_sent, resp_rdy, resp, clr_resp_rdy);

	input clk, rst_n;		// clock and active low reset
	input RX;				// serial data input
	input send_cmd;			// indicates to tranmit 24-bit command (cmd)
	input [7:0] cmd;		// 8-bit command to send
	input [15:0] data;		// 16-bit data that accompanies command
	input clr_resp_rdy;		// asserted in test bench to knock down resp_rdy

	output TX;				// serial data output
	output reg cmd_sent;		// indicates transmission of command complete
	output resp_rdy;		// indicates 8-bit response has been received
	output [7:0] resp;		// 8-bit response from DUT

	////////////////////////////////////////////////////
	// Declare any needed internal signals/registers //
	// below including state definitions            //
	/////////////////////////////////////////////////
	typedef enum reg [1:0] {init, transmitting_data1, transmitting_data2, transmitting_cmd} state_t;
	wire [7:0] tx_data;
	reg [1:0] curr_state, nxt_state;
	reg [7:0] top_data_line, bottom_data_line;
	reg [1:0] sel;
	reg trmt;

	///////////////////////////////////////////////
	// Instantiate basic 8-bit UART transceiver //
	/////////////////////////////////////////////
	UART iUART(.clk(clk), .rst_n(rst_n), .RX(RX), .TX(TX), .tx_data(tx_data), .trmt(trmt),
			   .tx_done(tx_done), .rx_data(resp), .rx_rdy(resp_rdy), .clr_rx_rdy(clr_resp_rdy));
		   
	/////////////////////////////////
	// Implement RemoteComm Below //
	///////////////////////////////

	// data storage flip flops
	always @(posedge clk, negedge rst_n)
		if (!rst_n)
			top_data_line <= 8'h00;
		else if (send_cmd) 
			top_data_line <= data[15:8];

	always @(posedge clk, negedge rst_n)
		if (!rst_n)
			bottom_data_line <= 8'h00;
		else if (send_cmd)
			bottom_data_line <= data[7:0];

	// mux for tx selection
	assign tx_data = (sel == 2'b00) ? cmd :
									(sel == 2'b01) ? top_data_line :
									bottom_data_line;

	// state transition block
	always_comb begin
		nxt_state = curr_state;
		trmt = 1'b0;
		cmd_sent = 1'b0;
		sel = 2'b00;
		casex (curr_state)
			transmitting_cmd:
				if (tx_done) begin // done transmitting byte, onto the next
					sel = 2'b01;
					trmt = 1'b1;
					nxt_state = transmitting_data1;
				end else begin
					sel = 2'b00;
				end
			transmitting_data1:
				if (tx_done) begin // done transmitting byte, onto the next
					sel = 2'b10;
					trmt = 1'b1;
					nxt_state = transmitting_data2;
				end else begin
					sel = 2'b01;
				end
			transmitting_data2:
				if (tx_done) begin // done transmitting byte, we're done
					cmd_sent = 1'b1;
					nxt_state = init;
				end else begin
					sel = 2'b10;
				end
			default: // init case
				if (send_cmd) begin
					sel = 2'b00;
					trmt = 1'b1;
					nxt_state = transmitting_cmd;
				end
		endcase
	end

	// curr_state flip flop
	always @(posedge clk, negedge rst_n)
		if (!rst_n)
			curr_state <= init;
		else
			curr_state <= nxt_state;

endmodule	

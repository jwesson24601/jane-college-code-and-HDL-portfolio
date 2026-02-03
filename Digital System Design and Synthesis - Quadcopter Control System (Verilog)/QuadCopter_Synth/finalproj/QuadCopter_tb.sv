module QuadCopter_tb();
			
  //// Interconnects to DUT/support defined as type wire /////
  wire SS_n, SCLK, MOSI, MISO, INT;
  wire RX, TX;
  wire [7:0] resp;				// response from DUT
  wire cmd_sent, resp_rdy;
  wire frnt_ESC, back_ESC, left_ESC, rght_ESC;

  ////// Stimulus is declared as type reg ///////
  reg clk, RST_n;
  reg [7:0] host_cmd;				// command host is sending to DUT
  reg [15:0] data;				// data associated with command
  reg send_cmd;					// asserted to initiate sending of command
  reg clr_resp_rdy;				// asserted to knock down resp_rdy

  //wire [7:0] LED;

  integer i, j, k; // for loop variables
  reg [7:0] cmd2send; // task input that allows us to manually pick a command

  //// Maybe define some localparams for command encoding ///

  ////////////////////////////////////////////////////////////////
  // Instantiate Physical Model of Copter with Inertial sensor //
  //////////////////////////////////////////////////////////////	
  CycloneIV iQuad(.clk(clk),.RST_n(RST_n),.SS_n(SS_n),.SCLK(SCLK),.MISO(MISO),
                  .MOSI(MOSI),.INT(INT),.frnt_ESC(frnt_ESC),.back_ESC(back_ESC),
                  .left_ESC(left_ESC),.rght_ESC(rght_ESC));				  			
    
    
  ////// Instantiate DUT ////////
  QuadCopter iDUT(.clk(clk),.RST_n(RST_n),.SS_n(SS_n),.SCLK(SCLK),.MOSI(MOSI),.MISO(MISO),
                  .INT(INT),.RX(RX),.TX(TX),/*.LED(LED),*/.FRNT(frnt_ESC),.BCK(back_ESC),
                  .LFT(left_ESC),.RGHT(rght_ESC));


  //// Instantiate Master UART (mimics host commands) //////
  RemoteComm iREMOTE(.clk(clk), .rst_n(RST_n), .RX(TX), .TX(RX),
                     .cmd(host_cmd), .data(data), .send_cmd(send_cmd),
                     .cmd_sent(cmd_sent), .resp_rdy(resp_rdy),
                     .resp(resp), .clr_resp_rdy(clr_resp_rdy));

  always
      #5 clk = ~clk;

  initial begin
    // three different testing suites - run one after the other and stop if we error out.
    simple_cmds();
    complex_cmds();
    landing_sequence();
    $stop();
  end

  // TASK BLOCK // (Rupel said to place the tasks in here instead of a separate file, for now)
  task initialize; // simple task to initialize the outputs of the DUT

    clk = 0;
    RST_n = 0;

    clr_resp_rdy = 1;
    send_cmd = 0;
    host_cmd = 8'h00;
    data = 16'h0000;

    @(posedge clk); // assert rst through a positive edge
    @(negedge clk); 
    RST_n = 1;

  endtask

  task get_airbourne; // get off the ground so that the cyclone can test setting pitch, roll, and yaw

    host_cmd = 8'h05; // set thrst
    data = 16'h00FF; // set to 0xFF - get off the ground
    send_cmd = 1;
    @(posedge clk); // send the command from the RemoteComm
    @(negedge clk);
    send_cmd = 0;

    rcv_ack(); // wait for the thrust to be acknowledged
  endtask

  task calibrate;

    host_cmd = 8'h06; // calibrate command
    data = 16'h0000;
    send_cmd = 1;
    @(posedge clk); // send the command from the RemoteComm
    @(negedge clk);
    send_cmd = 0;

    rcv_ack(); // wait to be acknowledged
  endtask

  task create_cmd; // set up RemoteComm to send a new comand and then send it

    if (cmd2send == 8'hff)
      host_cmd = ($urandom() % 8'h03) + 2; // randomize cmd and data
    else
      host_cmd = cmd2send;
    data = ($urandom() % 16'h0201) - 16'h0100; // get a value between positive and negative 256
    send_cmd = 1;
    @(posedge clk); // send the command from the RemoteComm
    @(negedge clk);
    send_cmd = 0;

  endtask

  task rcv_ack; // simple task to wait for a response to be received from cmd_cfg after sent from remote comm
  
    fork
      begin
        // test to see if the response made it out of the quadcopter
        @(posedge iDUT.resp_sent);
        disable timeout1;
      end
      
      begin : timeout1
        repeat (2000000) @(posedge clk); // timeout loop waiting for the positive ack
        $error("Timed out waiting for positive acknowledgement to be generated from cmd_cfg.\n");
        $stop();
      end
    join
    
  endtask

  task simple_cmds;
    cmd2send = 8'hff;
    initialize();
    calibrate();
    get_airbourne();
    for (i = 0; i < 16; i = i + 1) begin // send 16 random commands that set pitch, roll, and yaw
      $display("~~~ Cycle%d - simple test ~~~\n", i);
      create_cmd(); // randomize a command and send it via the remote
      rcv_ack(); // receive the acknowledgement from cmd_cfg
      repeat (2000000) @(posedge clk); // wait 10000000 clk cycles for the cyclone to read the pwm
      
      fork
        begin : wait_for_value
          repeat (10000000) @(posedge clk); // timeout loop waiting for the positive ack
          $error("Timed out waiting for the command %x to set the correct value.\n", host_cmd);
          $stop();
        end

        begin
          casex (host_cmd)
            8'h02: begin // PITCH CHECK
              while (!(iDUT.ptch >= data - 16'h0005 && iDUT.ptch <= data + 16'h0005)) begin
                // allow a range of 10 around data to be accepted - busy wait while the signal changes
              end
            end
            8'h03: // ROLL CHECK
              while (!(iDUT.roll >= data - 16'h0005 && iDUT.roll <= data + 16'h0005)) begin
                // allow a range of 10 around data to be accepted - busy wait while the signal changes
              end
            default: // YAW CHECK
              while (!(iDUT.yaw >= data - 16'h0005 && iDUT.yaw <= data + 16'h0005)) begin
                // allow a range of 10 around data to be accepted - busy wait while the signal changes
              end
          endcase

          $display("Cycle%d passed!\n", i);
          disable wait_for_value;
        end
      join

    end

    $display("Simple tests passed!\n");
  endtask

  task complex_cmds;
    $display("Starting Complex pitch/roll/yaw tests...\n");
    initialize();
    calibrate();
    get_airbourne();

    fork

      begin // PTCH SEND
        for (i = 0; i < 5; i = i + 1) begin // send 16 random commands that set pitch, roll, and yaw
          cmd2send = 8'h02;
          create_cmd();
          rcv_ack(); // receive the acknowledgement from cmd_cfg
          repeat (2000000) @(posedge clk); // wait 10000000 clk cycles for the cyclone to read the pwm
          
          fork

            begin : wait_for_value_ptch
              repeat (10000000) @(posedge clk); // timeout loop waiting for the positive ack
              $error("Timed out waiting for the command %x to set the correct value.\n", host_cmd);
              $stop();
            end

            begin
              while (!(iDUT.ptch >= data - 16'h0005 && iDUT.ptch <= data + 16'h0005)) begin
                // allow a range of 10 around data to be accepted - busy wait while the signal changes
              end
              $display("ptch correct... onto the next cycle\n");
              disable wait_for_value_ptch;
            end

          join

        end
      end

      begin // ROLL SEND
        repeat (666666) @(posedge clk); // offset the sending of roll from ptch by 1/3 of 2 million
        for (j = 0; j < 5; j = j + 1) begin // send 16 random commands that set pitch, roll, and yaw
          cmd2send = 8'h03;
          create_cmd();
          rcv_ack(); // receive the acknowledgement from cmd_cfg
          repeat (2000000) @(posedge clk); // wait 10000000 clk cycles for the cyclone to read the pwm
          
          fork

            begin : wait_for_value_roll
              repeat (10000000) @(posedge clk); // timeout loop waiting for the positive ack
              $error("Timed out waiting for the command %x to set the correct value.\n", host_cmd);
              $stop();
            end

            begin
              while (!(iDUT.roll >= data - 16'h0005 && iDUT.roll <= data + 16'h0005)) begin
                // allow a range of 10 around data to be accepted - busy wait while the signal changes
              end
              $display("roll correct... onto the next cycle\n");
              disable wait_for_value_roll;
            end
            
          join

        end
      end

      begin // YAW SEND
        repeat (1300000) @(posedge clk); // offset the sending of yaw from ptch by ~2/3 of 2 million
        for (k = 0; k < 5; k = k + 1) begin // send 16 random commands that set pitch, roll, and yaw
          cmd2send = 8'h04;
          create_cmd();
          rcv_ack(); // receive the acknowledgement from cmd_cfg
          repeat (2000000) @(posedge clk); // wait 10000000 clk cycles for the cyclone to read the pwm
          
          fork

            begin : wait_for_value_yaw
              repeat (10000000) @(posedge clk); // timeout loop waiting for the positive ack
              $error("Timed out waiting for the command %x to set the correct value.\n", host_cmd);
              $stop();
            end

            begin
              while (!(iDUT.yaw >= data - 16'h0005 && iDUT.yaw <= data + 16'h0005)) begin
                // allow a range of 10 around data to be accepted - busy wait while the signal changes
              end
              $display("yaw correct... onto the next cycle\n");
              disable wait_for_value_yaw;
            end
            
          join
        end
      end

    join

    $display("Complex pitch/roll/yaw tests passed!\n");
  endtask

  task landing_sequence; // tests a landing sequence and ALL extra instruction that aren't setting ptch, roll, or yaw
    $display("Starting Landing Sequence Testing...\n");
    cmd2send = 8'h07; // EMERGENCY LAND command
    create_cmd();
    rcv_ack();
    repeat (2000000) @(posedge clk);
    fork

      begin : timeout_emergency_land
        repeat (10000000) @(posedge clk);
        $error("Error waiting for the ptch roll and yaw to become 0 after emergency land is asserted.\n");
        $stop();
      end

      begin
        while (!(iDUT.ptch >= -10 && iDUT.ptch <= 10)) begin
          // busy wait while the signal changes
        end
        while (!(iDUT.roll >= -10 && iDUT.roll <= 10)) begin
          // busy wait while the signal changes
        end
        while (!(iDUT.yaw >= -10 && iDUT.yaw <= 10)) begin
          // busy wait while the signal changes
        end
        while (iDUT.thrst != 0) begin
          // busy wait for the thrust - should already be set though...
        end
        disable timeout_emergency_land;
      end

    join

    cmd2send = 8'h08; // MOTORS_OFF command
    create_cmd();
    rcv_ack();
    // check to see if all motors are off.
    fork

      begin : timeout_motors_off
        repeat (200000) @(posedge clk);
        $error("Error waiting for the motors to turn off after motors_off cmd is asserted.\n");
        $stop();
      end

      begin
        // busy wait on these values while we wait for them to turn off
        while (iDUT.frnt_spd != 0) begin
        end
        while (iDUT.bck_spd != 0) begin
        end
        while (iDUT.lft_spd != 0) begin
        end
        while (iDUT.rght_spd != 0) begin
        end
        disable timeout_motors_off;
      end

    join

    cmd2send = 8'h06; // Calibrate command
    create_cmd();
    rcv_ack();
    // test if the motor speeds are the CAL_SPEED from the flight control
    fork

      begin : timeout_calibrate
        repeat (200000) @(posedge clk);
        $error("Error waiting for the motors to turn off after motors_off cmd is asserted.\n");
        $stop();
      end

      begin
        // busy wait on these values while we wait for them to change to CAL_SPEED
        while (iDUT.frnt_spd != iDUT.ifly.CAL_SPEED) begin
        end
        while (iDUT.bck_spd != iDUT.ifly.CAL_SPEED) begin
        end
        while (iDUT.lft_spd != iDUT.ifly.CAL_SPEED) begin
        end
        while (iDUT.rght_spd != iDUT.ifly.CAL_SPEED) begin
        end
        disable timeout_calibrate;
      end

    join

    
    // time to relaunch again - test the thrust
    cmd2send = 8'h05;
    get_airbourne();
    repeat (5) @(posedge clk); // give the thrst some time to update
    if (iDUT.thrst != 9'h0FF) begin
      $display("Error, thrst was not set to 0xFF, but to %x\n", iDUT.thrst);
      $stop();
    end

    $display("Landing sequence passed!\n");
    $stop();

  endtask

endmodule
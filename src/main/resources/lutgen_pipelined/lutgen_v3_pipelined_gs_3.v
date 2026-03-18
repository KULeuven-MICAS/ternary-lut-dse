module LUTGen (
    input clock,
    input reset,
    input [7:0] in_0,
    input [7:0] in_1,
    input [7:0] in_2,
    output [9:0] out_0,
    output [9:0] out_1,
    output [9:0] out_2,
    output [9:0] out_3,
    output [9:0] out_4,
    output [9:0] out_5,
    output [9:0] out_6,
    output [9:0] out_7,
    output [9:0] out_8,
    output [9:0] out_9,
    output [9:0] out_10,
    output [9:0] out_11,
    output [9:0] out_12
);

reg [9:0] REG0_0;
reg [9:0] REG0_1;
reg [9:0] REG0_2;
reg [9:0] REG0_3;
reg [9:0] REG0_4;
reg [9:0] REG0_5;
reg [9:0] REG0_6;
reg [9:0] REG0_7;
reg [9:0] REG0_8;
reg [9:0] REG0_9;
reg [9:0] REG0_10;
reg [9:0] REG0_11;
reg [9:0] REG0_12;
reg [9:0] REG1_0;
reg [9:0] REG1_1;
reg [9:0] REG1_2;
reg [9:0] REG1_3;
reg [9:0] REG1_4;
reg [9:0] REG1_5;
reg [9:0] REG1_6;
reg [9:0] REG1_7;
reg [9:0] REG1_8;
reg [9:0] REG1_9;
reg [9:0] REG1_10;
reg [9:0] REG1_11;
reg [9:0] REG1_12;


always @(posedge clock or posedge reset) begin
   if (reset) begin
       REG0_0 <= 10'b0;
       REG0_1 <= 10'b0;
       REG0_2 <= 10'b0;
       REG0_3 <= 10'b0;
       REG0_4 <= 10'b0;
       REG0_5 <= 10'b0;
       REG0_6 <= 10'b0;
       REG0_7 <= 10'b0;
       REG0_8 <= 10'b0;
       REG0_9 <= 10'b0;
       REG0_10 <= 10'b0;
       REG0_11 <= 10'b0;
       REG0_12 <= 10'b0;
       REG1_0 <= 10'b0;
       REG1_1 <= 10'b0;
       REG1_2 <= 10'b0;
       REG1_3 <= 10'b0;
       REG1_4 <= 10'b0;
       REG1_5 <= 10'b0;
       REG1_6 <= 10'b0;
       REG1_7 <= 10'b0;
       REG1_8 <= 10'b0;
       REG1_9 <= 10'b0;
       REG1_10 <= 10'b0;
       REG1_11 <= 10'b0;
       REG1_12 <= 10'b0;
   end else begin
       REG0_0 <= $signed({{2{in_0[7]}}, in_0}) + REG1_0;
       REG0_1 <= $signed({{2{in_0[7]}}, in_0}) + REG1_1;
       REG0_2 <= $signed({{2{in_0[7]}}, in_0}) + REG1_2;
       REG0_3 <= $signed({{2{in_0[7]}}, in_0}) + REG1_3;
       REG0_4 <= $signed({{2{in_0[7]}}, in_0}) + REG1_4;
       REG0_5 <= $signed({{2{in_0[7]}}, in_0}) + REG1_5;
       REG0_6 <= $signed({{2{in_0[7]}}, in_0}) + REG1_6;
       REG0_7 <= $signed({{2{in_0[7]}}, in_0}) + REG1_7;
       REG0_8 <= $signed({{2{in_0[7]}}, in_0}) + REG1_8;
       REG0_9 <=  REG1_9;
       REG0_10 <=  REG1_10;
       REG0_11 <=  REG1_11;
       REG0_12 <=  REG1_12;
       REG1_0 <= $signed({{2{in_1[7]}}, in_1}) + $signed({{2{in_2[7]}}, in_2});
       REG1_1 <= $signed({{2{in_1[7]}}, in_1}) ;
       REG1_2 <= $signed({{2{in_1[7]}}, in_1}) - $signed({{2{in_2[7]}}, in_2});
       REG1_3 <= 10'b0 + $signed({{2{in_2[7]}}, in_2});
       REG1_4 <= 10'b0 ;
       REG1_5 <= 10'b0 - $signed({{2{in_2[7]}}, in_2});
       REG1_6 <= - $signed({{2{in_1[7]}}, in_1}) + $signed({{2{in_2[7]}}, in_2});
       REG1_7 <= - $signed({{2{in_1[7]}}, in_1}) ;
       REG1_8 <= - $signed({{2{in_1[7]}}, in_1}) - $signed({{2{in_2[7]}}, in_2});
       REG1_9 <= $signed({{2{in_1[7]}}, in_1}) + $signed({{2{in_2[7]}}, in_2});
       REG1_10 <= $signed({{2{in_1[7]}}, in_1}) ;
       REG1_11 <= $signed({{2{in_1[7]}}, in_1}) - $signed({{2{in_2[7]}}, in_2});
       REG1_12 <= 10'b0 + $signed({{2{in_2[7]}}, in_2});
   end
end

assign out_0 = REG0_0;
assign out_1 = REG0_1;
assign out_2 = REG0_2;
assign out_3 = REG0_3;
assign out_4 = REG0_4;
assign out_5 = REG0_5;
assign out_6 = REG0_6;
assign out_7 = REG0_7;
assign out_8 = REG0_8;
assign out_9 = REG0_9;
assign out_10 = REG0_10;
assign out_11 = REG0_11;
assign out_12 = REG0_12;


endmodule

module LUTGen (
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

assign out_0 =  $signed({{2{in_0[7]}}, in_0}) + $signed({{2{in_1[7]}}, in_1}) + $signed({{2{in_2[7]}}, in_2});
assign out_1 =  $signed({{2{in_0[7]}}, in_0}) + $signed({{2{in_1[7]}}, in_1});
assign out_2 =  $signed({{2{in_0[7]}}, in_0}) + $signed({{2{in_1[7]}}, in_1}) - $signed({{2{in_2[7]}}, in_2});
assign out_3 =  $signed({{2{in_0[7]}}, in_0})  + $signed({{2{in_2[7]}}, in_2});
assign out_4 =  $signed({{2{in_0[7]}}, in_0});
assign out_5 =  $signed({{2{in_0[7]}}, in_0})  - $signed({{2{in_2[7]}}, in_2});
assign out_6 =  $signed({{2{in_0[7]}}, in_0}) - $signed({{2{in_1[7]}}, in_1}) + $signed({{2{in_2[7]}}, in_2});
assign out_7 =  $signed({{2{in_0[7]}}, in_0}) - $signed({{2{in_1[7]}}, in_1});
assign out_8 =  $signed({{2{in_0[7]}}, in_0}) - $signed({{2{in_1[7]}}, in_1}) - $signed({{2{in_2[7]}}, in_2});
assign out_9 =  $signed({{2{in_1[7]}}, in_1}) + $signed({{2{in_2[7]}}, in_2});
assign out_10 =  $signed({{2{in_1[7]}}, in_1});
assign out_11 =  $signed({{2{in_1[7]}}, in_1}) - $signed({{2{in_2[7]}}, in_2});
assign out_12 =  $signed({{2{in_2[7]}}, in_2});
endmodule

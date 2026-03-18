module LUTGen (
    input [7:0] in_0,
    input [7:0] in_1,
    input [7:0] in_2,
    input [7:0] in_3,
    output [10:0] out_0,
    output [10:0] out_1,
    output [10:0] out_2,
    output [10:0] out_3,
    output [10:0] out_4,
    output [10:0] out_5,
    output [10:0] out_6,
    output [10:0] out_7,
    output [10:0] out_8,
    output [10:0] out_9,
    output [10:0] out_10,
    output [10:0] out_11,
    output [10:0] out_12,
    output [10:0] out_13,
    output [10:0] out_14,
    output [10:0] out_15,
    output [10:0] out_16,
    output [10:0] out_17,
    output [10:0] out_18,
    output [10:0] out_19,
    output [10:0] out_20,
    output [10:0] out_21,
    output [10:0] out_22,
    output [10:0] out_23,
    output [10:0] out_24,
    output [10:0] out_25,
    output [10:0] out_26,
    output [10:0] out_27,
    output [10:0] out_28,
    output [10:0] out_29,
    output [10:0] out_30,
    output [10:0] out_31,
    output [10:0] out_32,
    output [10:0] out_33,
    output [10:0] out_34,
    output [10:0] out_35,
    output [10:0] out_36,
    output [10:0] out_37,
    output [10:0] out_38,
    output [10:0] out_39
);

assign out_0 =  $signed({{3{in_0[7]}}, in_0}) + $signed({{3{in_1[7]}}, in_1}) + $signed({{3{in_2[7]}}, in_2}) + $signed({{3{in_3[7]}}, in_3});
assign out_1 =  $signed({{3{in_0[7]}}, in_0}) + $signed({{3{in_1[7]}}, in_1}) + $signed({{3{in_2[7]}}, in_2});
assign out_2 =  $signed({{3{in_0[7]}}, in_0}) + $signed({{3{in_1[7]}}, in_1}) + $signed({{3{in_2[7]}}, in_2}) - $signed({{3{in_3[7]}}, in_3});
assign out_3 =  $signed({{3{in_0[7]}}, in_0}) + $signed({{3{in_1[7]}}, in_1})  + $signed({{3{in_3[7]}}, in_3});
assign out_4 =  $signed({{3{in_0[7]}}, in_0}) + $signed({{3{in_1[7]}}, in_1});
assign out_5 =  $signed({{3{in_0[7]}}, in_0}) + $signed({{3{in_1[7]}}, in_1})  - $signed({{3{in_3[7]}}, in_3});
assign out_6 =  $signed({{3{in_0[7]}}, in_0}) + $signed({{3{in_1[7]}}, in_1}) - $signed({{3{in_2[7]}}, in_2}) + $signed({{3{in_3[7]}}, in_3});
assign out_7 =  $signed({{3{in_0[7]}}, in_0}) + $signed({{3{in_1[7]}}, in_1}) - $signed({{3{in_2[7]}}, in_2});
assign out_8 =  $signed({{3{in_0[7]}}, in_0}) + $signed({{3{in_1[7]}}, in_1}) - $signed({{3{in_2[7]}}, in_2}) - $signed({{3{in_3[7]}}, in_3});
assign out_9 =  $signed({{3{in_0[7]}}, in_0})  + $signed({{3{in_2[7]}}, in_2}) + $signed({{3{in_3[7]}}, in_3});
assign out_10 =  $signed({{3{in_0[7]}}, in_0})  + $signed({{3{in_2[7]}}, in_2});
assign out_11 =  $signed({{3{in_0[7]}}, in_0})  + $signed({{3{in_2[7]}}, in_2}) - $signed({{3{in_3[7]}}, in_3});
assign out_12 =  $signed({{3{in_0[7]}}, in_0})   + $signed({{3{in_3[7]}}, in_3});
assign out_13 =  $signed({{3{in_0[7]}}, in_0});
assign out_14 =  $signed({{3{in_0[7]}}, in_0})   - $signed({{3{in_3[7]}}, in_3});
assign out_15 =  $signed({{3{in_0[7]}}, in_0})  - $signed({{3{in_2[7]}}, in_2}) + $signed({{3{in_3[7]}}, in_3});
assign out_16 =  $signed({{3{in_0[7]}}, in_0})  - $signed({{3{in_2[7]}}, in_2});
assign out_17 =  $signed({{3{in_0[7]}}, in_0})  - $signed({{3{in_2[7]}}, in_2}) - $signed({{3{in_3[7]}}, in_3});
assign out_18 =  $signed({{3{in_0[7]}}, in_0}) - $signed({{3{in_1[7]}}, in_1}) + $signed({{3{in_2[7]}}, in_2}) + $signed({{3{in_3[7]}}, in_3});
assign out_19 =  $signed({{3{in_0[7]}}, in_0}) - $signed({{3{in_1[7]}}, in_1}) + $signed({{3{in_2[7]}}, in_2});
assign out_20 =  $signed({{3{in_0[7]}}, in_0}) - $signed({{3{in_1[7]}}, in_1}) + $signed({{3{in_2[7]}}, in_2}) - $signed({{3{in_3[7]}}, in_3});
assign out_21 =  $signed({{3{in_0[7]}}, in_0}) - $signed({{3{in_1[7]}}, in_1})  + $signed({{3{in_3[7]}}, in_3});
assign out_22 =  $signed({{3{in_0[7]}}, in_0}) - $signed({{3{in_1[7]}}, in_1});
assign out_23 =  $signed({{3{in_0[7]}}, in_0}) - $signed({{3{in_1[7]}}, in_1})  - $signed({{3{in_3[7]}}, in_3});
assign out_24 =  $signed({{3{in_0[7]}}, in_0}) - $signed({{3{in_1[7]}}, in_1}) - $signed({{3{in_2[7]}}, in_2}) + $signed({{3{in_3[7]}}, in_3});
assign out_25 =  $signed({{3{in_0[7]}}, in_0}) - $signed({{3{in_1[7]}}, in_1}) - $signed({{3{in_2[7]}}, in_2});
assign out_26 =  $signed({{3{in_0[7]}}, in_0}) - $signed({{3{in_1[7]}}, in_1}) - $signed({{3{in_2[7]}}, in_2}) - $signed({{3{in_3[7]}}, in_3});
assign out_27 =  $signed({{3{in_1[7]}}, in_1}) + $signed({{3{in_2[7]}}, in_2}) + $signed({{3{in_3[7]}}, in_3});
assign out_28 =  $signed({{3{in_1[7]}}, in_1}) + $signed({{3{in_2[7]}}, in_2});
assign out_29 =  $signed({{3{in_1[7]}}, in_1}) + $signed({{3{in_2[7]}}, in_2}) - $signed({{3{in_3[7]}}, in_3});
assign out_30 =  $signed({{3{in_1[7]}}, in_1})  + $signed({{3{in_3[7]}}, in_3});
assign out_31 =  $signed({{3{in_1[7]}}, in_1});
assign out_32 =  $signed({{3{in_1[7]}}, in_1})  - $signed({{3{in_3[7]}}, in_3});
assign out_33 =  $signed({{3{in_1[7]}}, in_1}) - $signed({{3{in_2[7]}}, in_2}) + $signed({{3{in_3[7]}}, in_3});
assign out_34 =  $signed({{3{in_1[7]}}, in_1}) - $signed({{3{in_2[7]}}, in_2});
assign out_35 =  $signed({{3{in_1[7]}}, in_1}) - $signed({{3{in_2[7]}}, in_2}) - $signed({{3{in_3[7]}}, in_3});
assign out_36 =  $signed({{3{in_2[7]}}, in_2}) + $signed({{3{in_3[7]}}, in_3});
assign out_37 =  $signed({{3{in_2[7]}}, in_2});
assign out_38 =  $signed({{3{in_2[7]}}, in_2}) - $signed({{3{in_3[7]}}, in_3});
assign out_39 =  $signed({{3{in_3[7]}}, in_3});
endmodule

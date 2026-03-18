module LUTGen (
    input clock,
    input reset,
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

reg [10:0] REG0_0;
reg [10:0] REG0_1;
reg [10:0] REG0_2;
reg [10:0] REG0_3;
reg [10:0] REG0_4;
reg [10:0] REG0_5;
reg [10:0] REG0_6;
reg [10:0] REG0_7;
reg [10:0] REG0_8;
reg [10:0] REG0_9;
reg [10:0] REG0_10;
reg [10:0] REG0_11;
reg [10:0] REG0_12;
reg [10:0] REG0_13;
reg [10:0] REG0_14;
reg [10:0] REG0_15;
reg [10:0] REG0_16;
reg [10:0] REG0_17;
reg [10:0] REG0_18;
reg [10:0] REG0_19;
reg [10:0] REG0_20;
reg [10:0] REG0_21;
reg [10:0] REG0_22;
reg [10:0] REG0_23;
reg [10:0] REG0_24;
reg [10:0] REG0_25;
reg [10:0] REG0_26;
reg [10:0] REG0_27;
reg [10:0] REG0_28;
reg [10:0] REG0_29;
reg [10:0] REG0_30;
reg [10:0] REG0_31;
reg [10:0] REG0_32;
reg [10:0] REG0_33;
reg [10:0] REG0_34;
reg [10:0] REG0_35;
reg [10:0] REG0_36;
reg [10:0] REG0_37;
reg [10:0] REG0_38;
reg [10:0] REG0_39;
reg [10:0] REG1_0;
reg [10:0] REG1_1;
reg [10:0] REG1_2;
reg [10:0] REG1_3;
reg [10:0] REG1_4;
reg [10:0] REG1_5;
reg [10:0] REG1_6;
reg [10:0] REG1_7;
reg [10:0] REG1_8;
reg [10:0] REG1_9;
reg [10:0] REG1_10;
reg [10:0] REG1_11;
reg [10:0] REG1_12;
reg [10:0] REG1_13;
reg [10:0] REG1_14;
reg [10:0] REG1_15;
reg [10:0] REG1_16;
reg [10:0] REG1_17;
reg [10:0] REG1_18;
reg [10:0] REG1_19;
reg [10:0] REG1_20;
reg [10:0] REG1_21;
reg [10:0] REG1_22;
reg [10:0] REG1_23;
reg [10:0] REG1_24;
reg [10:0] REG1_25;
reg [10:0] REG1_26;
reg [10:0] REG1_27;
reg [10:0] REG1_28;
reg [10:0] REG1_29;
reg [10:0] REG1_30;
reg [10:0] REG1_31;
reg [10:0] REG1_32;
reg [10:0] REG1_33;
reg [10:0] REG1_34;
reg [10:0] REG1_35;
reg [10:0] REG1_36;
reg [10:0] REG1_37;
reg [10:0] REG1_38;
reg [10:0] REG1_39;
reg [10:0] REG2_0;
reg [10:0] REG2_1;
reg [10:0] REG2_2;
reg [10:0] REG2_3;
reg [10:0] REG2_4;
reg [10:0] REG2_5;
reg [10:0] REG2_6;
reg [10:0] REG2_7;
reg [10:0] REG2_8;
reg [10:0] REG2_9;
reg [10:0] REG2_10;
reg [10:0] REG2_11;
reg [10:0] REG2_12;
reg [10:0] REG2_13;
reg [10:0] REG2_14;
reg [10:0] REG2_15;
reg [10:0] REG2_16;
reg [10:0] REG2_17;
reg [10:0] REG2_18;
reg [10:0] REG2_19;
reg [10:0] REG2_20;
reg [10:0] REG2_21;
reg [10:0] REG2_22;
reg [10:0] REG2_23;
reg [10:0] REG2_24;
reg [10:0] REG2_25;
reg [10:0] REG2_26;
reg [10:0] REG2_27;
reg [10:0] REG2_28;
reg [10:0] REG2_29;
reg [10:0] REG2_30;
reg [10:0] REG2_31;
reg [10:0] REG2_32;
reg [10:0] REG2_33;
reg [10:0] REG2_34;
reg [10:0] REG2_35;
reg [10:0] REG2_36;
reg [10:0] REG2_37;
reg [10:0] REG2_38;
reg [10:0] REG2_39;


always @(posedge clock or posedge reset) begin
   if (reset) begin
       REG0_0 <= 11'b0;
       REG0_1 <= 11'b0;
       REG0_2 <= 11'b0;
       REG0_3 <= 11'b0;
       REG0_4 <= 11'b0;
       REG0_5 <= 11'b0;
       REG0_6 <= 11'b0;
       REG0_7 <= 11'b0;
       REG0_8 <= 11'b0;
       REG0_9 <= 11'b0;
       REG0_10 <= 11'b0;
       REG0_11 <= 11'b0;
       REG0_12 <= 11'b0;
       REG0_13 <= 11'b0;
       REG0_14 <= 11'b0;
       REG0_15 <= 11'b0;
       REG0_16 <= 11'b0;
       REG0_17 <= 11'b0;
       REG0_18 <= 11'b0;
       REG0_19 <= 11'b0;
       REG0_20 <= 11'b0;
       REG0_21 <= 11'b0;
       REG0_22 <= 11'b0;
       REG0_23 <= 11'b0;
       REG0_24 <= 11'b0;
       REG0_25 <= 11'b0;
       REG0_26 <= 11'b0;
       REG0_27 <= 11'b0;
       REG0_28 <= 11'b0;
       REG0_29 <= 11'b0;
       REG0_30 <= 11'b0;
       REG0_31 <= 11'b0;
       REG0_32 <= 11'b0;
       REG0_33 <= 11'b0;
       REG0_34 <= 11'b0;
       REG0_35 <= 11'b0;
       REG0_36 <= 11'b0;
       REG0_37 <= 11'b0;
       REG0_38 <= 11'b0;
       REG0_39 <= 11'b0;
       REG1_0 <= 11'b0;
       REG1_1 <= 11'b0;
       REG1_2 <= 11'b0;
       REG1_3 <= 11'b0;
       REG1_4 <= 11'b0;
       REG1_5 <= 11'b0;
       REG1_6 <= 11'b0;
       REG1_7 <= 11'b0;
       REG1_8 <= 11'b0;
       REG1_9 <= 11'b0;
       REG1_10 <= 11'b0;
       REG1_11 <= 11'b0;
       REG1_12 <= 11'b0;
       REG1_13 <= 11'b0;
       REG1_14 <= 11'b0;
       REG1_15 <= 11'b0;
       REG1_16 <= 11'b0;
       REG1_17 <= 11'b0;
       REG1_18 <= 11'b0;
       REG1_19 <= 11'b0;
       REG1_20 <= 11'b0;
       REG1_21 <= 11'b0;
       REG1_22 <= 11'b0;
       REG1_23 <= 11'b0;
       REG1_24 <= 11'b0;
       REG1_25 <= 11'b0;
       REG1_26 <= 11'b0;
       REG1_27 <= 11'b0;
       REG1_28 <= 11'b0;
       REG1_29 <= 11'b0;
       REG1_30 <= 11'b0;
       REG1_31 <= 11'b0;
       REG1_32 <= 11'b0;
       REG1_33 <= 11'b0;
       REG1_34 <= 11'b0;
       REG1_35 <= 11'b0;
       REG1_36 <= 11'b0;
       REG1_37 <= 11'b0;
       REG1_38 <= 11'b0;
       REG1_39 <= 11'b0;
       REG2_0 <= 11'b0;
       REG2_1 <= 11'b0;
       REG2_2 <= 11'b0;
       REG2_3 <= 11'b0;
       REG2_4 <= 11'b0;
       REG2_5 <= 11'b0;
       REG2_6 <= 11'b0;
       REG2_7 <= 11'b0;
       REG2_8 <= 11'b0;
       REG2_9 <= 11'b0;
       REG2_10 <= 11'b0;
       REG2_11 <= 11'b0;
       REG2_12 <= 11'b0;
       REG2_13 <= 11'b0;
       REG2_14 <= 11'b0;
       REG2_15 <= 11'b0;
       REG2_16 <= 11'b0;
       REG2_17 <= 11'b0;
       REG2_18 <= 11'b0;
       REG2_19 <= 11'b0;
       REG2_20 <= 11'b0;
       REG2_21 <= 11'b0;
       REG2_22 <= 11'b0;
       REG2_23 <= 11'b0;
       REG2_24 <= 11'b0;
       REG2_25 <= 11'b0;
       REG2_26 <= 11'b0;
       REG2_27 <= 11'b0;
       REG2_28 <= 11'b0;
       REG2_29 <= 11'b0;
       REG2_30 <= 11'b0;
       REG2_31 <= 11'b0;
       REG2_32 <= 11'b0;
       REG2_33 <= 11'b0;
       REG2_34 <= 11'b0;
       REG2_35 <= 11'b0;
       REG2_36 <= 11'b0;
       REG2_37 <= 11'b0;
       REG2_38 <= 11'b0;
       REG2_39 <= 11'b0;
   end else begin
       REG0_0 <= $signed({{3{in_0[7]}}, in_0}) + REG1_0;
       REG0_1 <= $signed({{3{in_0[7]}}, in_0}) + REG1_1;
       REG0_2 <= $signed({{3{in_0[7]}}, in_0}) + REG1_2;
       REG0_3 <= $signed({{3{in_0[7]}}, in_0}) + REG1_3;
       REG0_4 <= $signed({{3{in_0[7]}}, in_0}) + REG1_4;
       REG0_5 <= $signed({{3{in_0[7]}}, in_0}) + REG1_5;
       REG0_6 <= $signed({{3{in_0[7]}}, in_0}) + REG1_6;
       REG0_7 <= $signed({{3{in_0[7]}}, in_0}) + REG1_7;
       REG0_8 <= $signed({{3{in_0[7]}}, in_0}) + REG1_8;
       REG0_9 <= $signed({{3{in_0[7]}}, in_0}) + REG1_9;
       REG0_10 <= $signed({{3{in_0[7]}}, in_0}) + REG1_10;
       REG0_11 <= $signed({{3{in_0[7]}}, in_0}) + REG1_11;
       REG0_12 <= $signed({{3{in_0[7]}}, in_0}) + REG1_12;
       REG0_13 <= $signed({{3{in_0[7]}}, in_0}) + REG1_13;
       REG0_14 <= $signed({{3{in_0[7]}}, in_0}) + REG1_14;
       REG0_15 <= $signed({{3{in_0[7]}}, in_0}) + REG1_15;
       REG0_16 <= $signed({{3{in_0[7]}}, in_0}) + REG1_16;
       REG0_17 <= $signed({{3{in_0[7]}}, in_0}) + REG1_17;
       REG0_18 <= $signed({{3{in_0[7]}}, in_0}) + REG1_18;
       REG0_19 <= $signed({{3{in_0[7]}}, in_0}) + REG1_19;
       REG0_20 <= $signed({{3{in_0[7]}}, in_0}) + REG1_20;
       REG0_21 <= $signed({{3{in_0[7]}}, in_0}) + REG1_21;
       REG0_22 <= $signed({{3{in_0[7]}}, in_0}) + REG1_22;
       REG0_23 <= $signed({{3{in_0[7]}}, in_0}) + REG1_23;
       REG0_24 <= $signed({{3{in_0[7]}}, in_0}) + REG1_24;
       REG0_25 <= $signed({{3{in_0[7]}}, in_0}) + REG1_25;
       REG0_26 <= $signed({{3{in_0[7]}}, in_0}) + REG1_26;
       REG0_27 <=  REG1_27;
       REG0_28 <=  REG1_28;
       REG0_29 <=  REG1_29;
       REG0_30 <=  REG1_30;
       REG0_31 <=  REG1_31;
       REG0_32 <=  REG1_32;
       REG0_33 <=  REG1_33;
       REG0_34 <=  REG1_34;
       REG0_35 <=  REG1_35;
       REG0_36 <=  REG1_36;
       REG0_37 <=  REG1_37;
       REG0_38 <=  REG1_38;
       REG0_39 <=  REG1_39;
       REG1_0 <= $signed({{3{in_1[7]}}, in_1}) + REG2_0;
       REG1_1 <= $signed({{3{in_1[7]}}, in_1}) + REG2_1;
       REG1_2 <= $signed({{3{in_1[7]}}, in_1}) + REG2_2;
       REG1_3 <= $signed({{3{in_1[7]}}, in_1}) + REG2_3;
       REG1_4 <= $signed({{3{in_1[7]}}, in_1}) + REG2_4;
       REG1_5 <= $signed({{3{in_1[7]}}, in_1}) + REG2_5;
       REG1_6 <= $signed({{3{in_1[7]}}, in_1}) + REG2_6;
       REG1_7 <= $signed({{3{in_1[7]}}, in_1}) + REG2_7;
       REG1_8 <= $signed({{3{in_1[7]}}, in_1}) + REG2_8;
       REG1_9 <=  REG2_9;
       REG1_10 <=  REG2_10;
       REG1_11 <=  REG2_11;
       REG1_12 <=  REG2_12;
       REG1_13 <=  REG2_13;
       REG1_14 <=  REG2_14;
       REG1_15 <=  REG2_15;
       REG1_16 <=  REG2_16;
       REG1_17 <=  REG2_17;
       REG1_18 <= - $signed({{3{in_1[7]}}, in_1}) + REG2_18;
       REG1_19 <= - $signed({{3{in_1[7]}}, in_1}) + REG2_19;
       REG1_20 <= - $signed({{3{in_1[7]}}, in_1}) + REG2_20;
       REG1_21 <= - $signed({{3{in_1[7]}}, in_1}) + REG2_21;
       REG1_22 <= - $signed({{3{in_1[7]}}, in_1}) + REG2_22;
       REG1_23 <= - $signed({{3{in_1[7]}}, in_1}) + REG2_23;
       REG1_24 <= - $signed({{3{in_1[7]}}, in_1}) + REG2_24;
       REG1_25 <= - $signed({{3{in_1[7]}}, in_1}) + REG2_25;
       REG1_26 <= - $signed({{3{in_1[7]}}, in_1}) + REG2_26;
       REG1_27 <= $signed({{3{in_1[7]}}, in_1}) + REG2_27;
       REG1_28 <= $signed({{3{in_1[7]}}, in_1}) + REG2_28;
       REG1_29 <= $signed({{3{in_1[7]}}, in_1}) + REG2_29;
       REG1_30 <= $signed({{3{in_1[7]}}, in_1}) + REG2_30;
       REG1_31 <= $signed({{3{in_1[7]}}, in_1}) + REG2_31;
       REG1_32 <= $signed({{3{in_1[7]}}, in_1}) + REG2_32;
       REG1_33 <= $signed({{3{in_1[7]}}, in_1}) + REG2_33;
       REG1_34 <= $signed({{3{in_1[7]}}, in_1}) + REG2_34;
       REG1_35 <= $signed({{3{in_1[7]}}, in_1}) + REG2_35;
       REG1_36 <=  REG2_36;
       REG1_37 <=  REG2_37;
       REG1_38 <=  REG2_38;
       REG1_39 <=  REG2_39;
       REG2_0 <= $signed({{3{in_2[7]}}, in_2}) + $signed({{3{in_3[7]}}, in_3});
       REG2_1 <= $signed({{3{in_2[7]}}, in_2}) ;
       REG2_2 <= $signed({{3{in_2[7]}}, in_2}) - $signed({{3{in_3[7]}}, in_3});
       REG2_3 <= 11'b0 + $signed({{3{in_3[7]}}, in_3});
       REG2_4 <= 11'b0 ;
       REG2_5 <= 11'b0 - $signed({{3{in_3[7]}}, in_3});
       REG2_6 <= - $signed({{3{in_2[7]}}, in_2}) + $signed({{3{in_3[7]}}, in_3});
       REG2_7 <= - $signed({{3{in_2[7]}}, in_2}) ;
       REG2_8 <= - $signed({{3{in_2[7]}}, in_2}) - $signed({{3{in_3[7]}}, in_3});
       REG2_9 <= $signed({{3{in_2[7]}}, in_2}) + $signed({{3{in_3[7]}}, in_3});
       REG2_10 <= $signed({{3{in_2[7]}}, in_2}) ;
       REG2_11 <= $signed({{3{in_2[7]}}, in_2}) - $signed({{3{in_3[7]}}, in_3});
       REG2_12 <= 11'b0 + $signed({{3{in_3[7]}}, in_3});
       REG2_13 <= 11'b0 ;
       REG2_14 <= 11'b0 - $signed({{3{in_3[7]}}, in_3});
       REG2_15 <= - $signed({{3{in_2[7]}}, in_2}) + $signed({{3{in_3[7]}}, in_3});
       REG2_16 <= - $signed({{3{in_2[7]}}, in_2}) ;
       REG2_17 <= - $signed({{3{in_2[7]}}, in_2}) - $signed({{3{in_3[7]}}, in_3});
       REG2_18 <= $signed({{3{in_2[7]}}, in_2}) + $signed({{3{in_3[7]}}, in_3});
       REG2_19 <= $signed({{3{in_2[7]}}, in_2}) ;
       REG2_20 <= $signed({{3{in_2[7]}}, in_2}) - $signed({{3{in_3[7]}}, in_3});
       REG2_21 <= 11'b0 + $signed({{3{in_3[7]}}, in_3});
       REG2_22 <= 11'b0 ;
       REG2_23 <= 11'b0 - $signed({{3{in_3[7]}}, in_3});
       REG2_24 <= - $signed({{3{in_2[7]}}, in_2}) + $signed({{3{in_3[7]}}, in_3});
       REG2_25 <= - $signed({{3{in_2[7]}}, in_2}) ;
       REG2_26 <= - $signed({{3{in_2[7]}}, in_2}) - $signed({{3{in_3[7]}}, in_3});
       REG2_27 <= $signed({{3{in_2[7]}}, in_2}) + $signed({{3{in_3[7]}}, in_3});
       REG2_28 <= $signed({{3{in_2[7]}}, in_2}) ;
       REG2_29 <= $signed({{3{in_2[7]}}, in_2}) - $signed({{3{in_3[7]}}, in_3});
       REG2_30 <= 11'b0 + $signed({{3{in_3[7]}}, in_3});
       REG2_31 <= 11'b0 ;
       REG2_32 <= 11'b0 - $signed({{3{in_3[7]}}, in_3});
       REG2_33 <= - $signed({{3{in_2[7]}}, in_2}) + $signed({{3{in_3[7]}}, in_3});
       REG2_34 <= - $signed({{3{in_2[7]}}, in_2}) ;
       REG2_35 <= - $signed({{3{in_2[7]}}, in_2}) - $signed({{3{in_3[7]}}, in_3});
       REG2_36 <= $signed({{3{in_2[7]}}, in_2}) + $signed({{3{in_3[7]}}, in_3});
       REG2_37 <= $signed({{3{in_2[7]}}, in_2}) ;
       REG2_38 <= $signed({{3{in_2[7]}}, in_2}) - $signed({{3{in_3[7]}}, in_3});
       REG2_39 <= 11'b0 + $signed({{3{in_3[7]}}, in_3});
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
assign out_13 = REG0_13;
assign out_14 = REG0_14;
assign out_15 = REG0_15;
assign out_16 = REG0_16;
assign out_17 = REG0_17;
assign out_18 = REG0_18;
assign out_19 = REG0_19;
assign out_20 = REG0_20;
assign out_21 = REG0_21;
assign out_22 = REG0_22;
assign out_23 = REG0_23;
assign out_24 = REG0_24;
assign out_25 = REG0_25;
assign out_26 = REG0_26;
assign out_27 = REG0_27;
assign out_28 = REG0_28;
assign out_29 = REG0_29;
assign out_30 = REG0_30;
assign out_31 = REG0_31;
assign out_32 = REG0_32;
assign out_33 = REG0_33;
assign out_34 = REG0_34;
assign out_35 = REG0_35;
assign out_36 = REG0_36;
assign out_37 = REG0_37;
assign out_38 = REG0_38;
assign out_39 = REG0_39;


endmodule

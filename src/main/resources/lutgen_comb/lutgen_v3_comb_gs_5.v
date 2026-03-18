module LUTGen (
    input [7:0] in_0,
    input [7:0] in_1,
    input [7:0] in_2,
    input [7:0] in_3,
    input [7:0] in_4,
    output [11:0] out_0,
    output [11:0] out_1,
    output [11:0] out_2,
    output [11:0] out_3,
    output [11:0] out_4,
    output [11:0] out_5,
    output [11:0] out_6,
    output [11:0] out_7,
    output [11:0] out_8,
    output [11:0] out_9,
    output [11:0] out_10,
    output [11:0] out_11,
    output [11:0] out_12,
    output [11:0] out_13,
    output [11:0] out_14,
    output [11:0] out_15,
    output [11:0] out_16,
    output [11:0] out_17,
    output [11:0] out_18,
    output [11:0] out_19,
    output [11:0] out_20,
    output [11:0] out_21,
    output [11:0] out_22,
    output [11:0] out_23,
    output [11:0] out_24,
    output [11:0] out_25,
    output [11:0] out_26,
    output [11:0] out_27,
    output [11:0] out_28,
    output [11:0] out_29,
    output [11:0] out_30,
    output [11:0] out_31,
    output [11:0] out_32,
    output [11:0] out_33,
    output [11:0] out_34,
    output [11:0] out_35,
    output [11:0] out_36,
    output [11:0] out_37,
    output [11:0] out_38,
    output [11:0] out_39,
    output [11:0] out_40,
    output [11:0] out_41,
    output [11:0] out_42,
    output [11:0] out_43,
    output [11:0] out_44,
    output [11:0] out_45,
    output [11:0] out_46,
    output [11:0] out_47,
    output [11:0] out_48,
    output [11:0] out_49,
    output [11:0] out_50,
    output [11:0] out_51,
    output [11:0] out_52,
    output [11:0] out_53,
    output [11:0] out_54,
    output [11:0] out_55,
    output [11:0] out_56,
    output [11:0] out_57,
    output [11:0] out_58,
    output [11:0] out_59,
    output [11:0] out_60,
    output [11:0] out_61,
    output [11:0] out_62,
    output [11:0] out_63,
    output [11:0] out_64,
    output [11:0] out_65,
    output [11:0] out_66,
    output [11:0] out_67,
    output [11:0] out_68,
    output [11:0] out_69,
    output [11:0] out_70,
    output [11:0] out_71,
    output [11:0] out_72,
    output [11:0] out_73,
    output [11:0] out_74,
    output [11:0] out_75,
    output [11:0] out_76,
    output [11:0] out_77,
    output [11:0] out_78,
    output [11:0] out_79,
    output [11:0] out_80,
    output [11:0] out_81,
    output [11:0] out_82,
    output [11:0] out_83,
    output [11:0] out_84,
    output [11:0] out_85,
    output [11:0] out_86,
    output [11:0] out_87,
    output [11:0] out_88,
    output [11:0] out_89,
    output [11:0] out_90,
    output [11:0] out_91,
    output [11:0] out_92,
    output [11:0] out_93,
    output [11:0] out_94,
    output [11:0] out_95,
    output [11:0] out_96,
    output [11:0] out_97,
    output [11:0] out_98,
    output [11:0] out_99,
    output [11:0] out_100,
    output [11:0] out_101,
    output [11:0] out_102,
    output [11:0] out_103,
    output [11:0] out_104,
    output [11:0] out_105,
    output [11:0] out_106,
    output [11:0] out_107,
    output [11:0] out_108,
    output [11:0] out_109,
    output [11:0] out_110,
    output [11:0] out_111,
    output [11:0] out_112,
    output [11:0] out_113,
    output [11:0] out_114,
    output [11:0] out_115,
    output [11:0] out_116,
    output [11:0] out_117,
    output [11:0] out_118,
    output [11:0] out_119,
    output [11:0] out_120
);

assign out_0 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_1 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3});
assign out_2 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_3 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2})  + $signed({{4{in_4[7]}}, in_4});
assign out_4 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2});
assign out_5 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2})  - $signed({{4{in_4[7]}}, in_4});
assign out_6 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_7 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3});
assign out_8 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_9 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1})  + $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_10 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1})  + $signed({{4{in_3[7]}}, in_3});
assign out_11 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1})  + $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_12 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1})   + $signed({{4{in_4[7]}}, in_4});
assign out_13 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1});
assign out_14 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1})   - $signed({{4{in_4[7]}}, in_4});
assign out_15 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1})  - $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_16 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1})  - $signed({{4{in_3[7]}}, in_3});
assign out_17 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1})  - $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_18 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_19 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3});
assign out_20 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_21 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2})  + $signed({{4{in_4[7]}}, in_4});
assign out_22 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2});
assign out_23 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2})  - $signed({{4{in_4[7]}}, in_4});
assign out_24 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_25 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3});
assign out_26 =  $signed({{4{in_0[7]}}, in_0}) + $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_27 =  $signed({{4{in_0[7]}}, in_0})  + $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_28 =  $signed({{4{in_0[7]}}, in_0})  + $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3});
assign out_29 =  $signed({{4{in_0[7]}}, in_0})  + $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_30 =  $signed({{4{in_0[7]}}, in_0})  + $signed({{4{in_2[7]}}, in_2})  + $signed({{4{in_4[7]}}, in_4});
assign out_31 =  $signed({{4{in_0[7]}}, in_0})  + $signed({{4{in_2[7]}}, in_2});
assign out_32 =  $signed({{4{in_0[7]}}, in_0})  + $signed({{4{in_2[7]}}, in_2})  - $signed({{4{in_4[7]}}, in_4});
assign out_33 =  $signed({{4{in_0[7]}}, in_0})  + $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_34 =  $signed({{4{in_0[7]}}, in_0})  + $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3});
assign out_35 =  $signed({{4{in_0[7]}}, in_0})  + $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_36 =  $signed({{4{in_0[7]}}, in_0})   + $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_37 =  $signed({{4{in_0[7]}}, in_0})   + $signed({{4{in_3[7]}}, in_3});
assign out_38 =  $signed({{4{in_0[7]}}, in_0})   + $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_39 =  $signed({{4{in_0[7]}}, in_0})    + $signed({{4{in_4[7]}}, in_4});
assign out_40 =  $signed({{4{in_0[7]}}, in_0});
assign out_41 =  $signed({{4{in_0[7]}}, in_0})    - $signed({{4{in_4[7]}}, in_4});
assign out_42 =  $signed({{4{in_0[7]}}, in_0})   - $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_43 =  $signed({{4{in_0[7]}}, in_0})   - $signed({{4{in_3[7]}}, in_3});
assign out_44 =  $signed({{4{in_0[7]}}, in_0})   - $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_45 =  $signed({{4{in_0[7]}}, in_0})  - $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_46 =  $signed({{4{in_0[7]}}, in_0})  - $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3});
assign out_47 =  $signed({{4{in_0[7]}}, in_0})  - $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_48 =  $signed({{4{in_0[7]}}, in_0})  - $signed({{4{in_2[7]}}, in_2})  + $signed({{4{in_4[7]}}, in_4});
assign out_49 =  $signed({{4{in_0[7]}}, in_0})  - $signed({{4{in_2[7]}}, in_2});
assign out_50 =  $signed({{4{in_0[7]}}, in_0})  - $signed({{4{in_2[7]}}, in_2})  - $signed({{4{in_4[7]}}, in_4});
assign out_51 =  $signed({{4{in_0[7]}}, in_0})  - $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_52 =  $signed({{4{in_0[7]}}, in_0})  - $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3});
assign out_53 =  $signed({{4{in_0[7]}}, in_0})  - $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_54 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_55 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3});
assign out_56 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_57 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2})  + $signed({{4{in_4[7]}}, in_4});
assign out_58 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2});
assign out_59 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2})  - $signed({{4{in_4[7]}}, in_4});
assign out_60 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_61 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3});
assign out_62 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_63 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1})  + $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_64 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1})  + $signed({{4{in_3[7]}}, in_3});
assign out_65 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1})  + $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_66 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1})   + $signed({{4{in_4[7]}}, in_4});
assign out_67 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1});
assign out_68 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1})   - $signed({{4{in_4[7]}}, in_4});
assign out_69 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1})  - $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_70 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1})  - $signed({{4{in_3[7]}}, in_3});
assign out_71 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1})  - $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_72 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_73 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3});
assign out_74 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_75 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2})  + $signed({{4{in_4[7]}}, in_4});
assign out_76 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2});
assign out_77 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2})  - $signed({{4{in_4[7]}}, in_4});
assign out_78 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_79 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3});
assign out_80 =  $signed({{4{in_0[7]}}, in_0}) - $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_81 =  $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_82 =  $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3});
assign out_83 =  $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_84 =  $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2})  + $signed({{4{in_4[7]}}, in_4});
assign out_85 =  $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2});
assign out_86 =  $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2})  - $signed({{4{in_4[7]}}, in_4});
assign out_87 =  $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_88 =  $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3});
assign out_89 =  $signed({{4{in_1[7]}}, in_1}) + $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_90 =  $signed({{4{in_1[7]}}, in_1})  + $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_91 =  $signed({{4{in_1[7]}}, in_1})  + $signed({{4{in_3[7]}}, in_3});
assign out_92 =  $signed({{4{in_1[7]}}, in_1})  + $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_93 =  $signed({{4{in_1[7]}}, in_1})   + $signed({{4{in_4[7]}}, in_4});
assign out_94 =  $signed({{4{in_1[7]}}, in_1});
assign out_95 =  $signed({{4{in_1[7]}}, in_1})   - $signed({{4{in_4[7]}}, in_4});
assign out_96 =  $signed({{4{in_1[7]}}, in_1})  - $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_97 =  $signed({{4{in_1[7]}}, in_1})  - $signed({{4{in_3[7]}}, in_3});
assign out_98 =  $signed({{4{in_1[7]}}, in_1})  - $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_99 =  $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_100 =  $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3});
assign out_101 =  $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_102 =  $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2})  + $signed({{4{in_4[7]}}, in_4});
assign out_103 =  $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2});
assign out_104 =  $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2})  - $signed({{4{in_4[7]}}, in_4});
assign out_105 =  $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_106 =  $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3});
assign out_107 =  $signed({{4{in_1[7]}}, in_1}) - $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_108 =  $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_109 =  $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3});
assign out_110 =  $signed({{4{in_2[7]}}, in_2}) + $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_111 =  $signed({{4{in_2[7]}}, in_2})  + $signed({{4{in_4[7]}}, in_4});
assign out_112 =  $signed({{4{in_2[7]}}, in_2});
assign out_113 =  $signed({{4{in_2[7]}}, in_2})  - $signed({{4{in_4[7]}}, in_4});
assign out_114 =  $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_115 =  $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3});
assign out_116 =  $signed({{4{in_2[7]}}, in_2}) - $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_117 =  $signed({{4{in_3[7]}}, in_3}) + $signed({{4{in_4[7]}}, in_4});
assign out_118 =  $signed({{4{in_3[7]}}, in_3});
assign out_119 =  $signed({{4{in_3[7]}}, in_3}) - $signed({{4{in_4[7]}}, in_4});
assign out_120 =  $signed({{4{in_4[7]}}, in_4});
endmodule

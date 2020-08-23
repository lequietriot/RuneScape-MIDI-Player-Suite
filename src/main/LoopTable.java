package main;

public class LoopTable {

    public static String RUNESCAPE_VERSION = "OSRS";

    public static int getLoopStart(int sampleID) {

        switch (RUNESCAPE_VERSION) {
            case "RS2":
                return getRS2LoopStart(sampleID);
            case "OSRS":
                return getOSLoopStart(sampleID);
            case "RSHD":
                return getHDLoopStart(sampleID);
            case "RS3":
                return getRS3LoopStart(sampleID);
        }

        return 0;
    }

    private static int getRS2LoopStart(int sampleID) {
        //Unfinished
        switch (sampleID) {

            //Patch 60
            case 157:
                return 24281;
            case 158:
                return 43418;
            case 159:
                return 60642;
        }

        return 0;
    }

    private static int getOSLoopStart(int sampleID) {

        //Unfinished
        switch (sampleID) {

            //Patch 0
            case 2:
                return 32861;
            case 3:
                return 15224;
            case 4:
                return 6092;
            case 5:
                return 12091;
            case 6:
                return 10570;

            //Patch 1
            case 7:
                return 40741;
            case 8:
                return 30374;
            case 9:
                return 19799;

            //Patch 2
            case 1:
                return 23323;
            case 11:
                return 17116;

            //Patch 3
            case 12:
                return 36605;
            case 13:
                return 12415;

            //Patch 4
            case 14:
                return 22255;
            case 15:
                return 6438;
            case 16:
                return 5421;

            //Patch 5
            case 17:
                return 14206;
            case 18:
                return 7885;
            case 19:
                return 19901;
            case 20:
                return 16933;

            //Patch 6
            case 21:
                return 66357;
            case 22:
                return 25691;
            case 23:
                return 18049;

            //Patch 7
            case 24:
                return 14083;
            case 25:
                return 9463;

            //Patch 8
            case 26:
                return 9361;

            //Patch 9
            case 27:
                return 10977;

            //Patch 10
            case 28:
                return 10084;

            //Patch 11
            case 29:
                return 15635;
            case 30:
                return 9306;

            //Patch 12 - No loop

            //Patch 13 - No loop

            //Patch 14
            case 33:
                return 106094;
            case 34:
                return 52674;
            case 35:
                return 36568;

            //Patch 15 - No loop

            //Patch 16
            case 39:
                return 21467;
            case 40:
                return 8992;

            //Patch 17
            case 41:
                return 18469;
            case 42:
                return 12796;
            case 43:
                return 5719;

            //Patch 18
            case 10:
                return 9875;
            case 44:
                return 9510;
            case 45:
                return 11875;
            case 46:
                return 10744;
            case 47:
                return 3668;

            //Patch 19
            case 48:
                return 25414;
            case 49:
                return 21827;
            case 50:
                return 20456;
            case 51:
                return 18469;
            case 52:
                return 13790;

            //Patch 20
            case 53:
                return 17358;
            case 54:
                return 9285;
            case 55:
                return 7082;

            //Patch 21
            case 56:
                return 22432;
            case 57:
                return 10654;
            case 58:
                return 12915;
            case 59:
                return 6051;

            //Patch 22
            case 60:
                return 8085;
            case 61:
                return 7680;
            case 62:
                return 4725;

            //Patch 23
            case 63:
                return 12317;
            case 64:
                return 6592;
            case 65:
                return 15437;

            //Patch 24
            case 66:
                return 18151;
            case 67:
                return 17916;
            case 68:
                return 12967;
            case 69:
                return 5371;

            //Patch 25
            case 70:
                return 36903;
            case 71:
                return 23413;
            case 72:
                return 21720;

            //Patch 26
            case 73:
                return 25234;

            //Patch 27
            case 74:
                return 55422;
            case 75:
                return 54632;
            case 76:
                return 41894;
            case 77:
                return 20334;

            //Patch 28
            case 78:
                return 3289;

            //Patch 29
            case 79:
                return 31769;
            case 80:
                return 26242;
            case 81:
                return 24442;
            case 82:
                return 21972;

            //Patch 30
            case 83:
                return 46029;
            case 84:
                return 41492;
            case 85:
                return 16168;
            case 86:
                return 12039;

            //Patch 31
            case 87:
                return 2483;

            //Patch 32
            case 88:
                return 14771;
            case 89:
                return 19087;

            //Patch 33
            case 90:
                return 19559;

            //Patch 34
            case 91:
                return 12190;
        }

        return 0;
    }

    public static int getHDLoopStart(int sampleID) {

        switch (sampleID) {

            //Patch 0
            case 133:
                return 22764;
            case 169:
                return 11508;
            case 145:
                return 11508;
            case 2:
                return 32861;
            case 173:
                return 26242;
            case 3:
                return 15224;
            case 60:
                return 13040;
            case 4:
                return 6092;
            case 154:
                return 18225;
            case 5:
                return 12091;
            case 6:
                return 10570;

            //Patch 1
            case 7:
                return 40741;
            case 297:
                return 39451;
            case 8:
                return 30374;
            case 302:
                return 22381;
            case 9:
                return 19799;
            case 17:
                return 15374;

            //Patch 2
            case 489:
                return 50818;
            case 179:
                return 27295;
            case 1:
                return 42292;
            case 116:
                return 24753;

            //Patch 3
            case 176:
                return 39618;
            case 299:
                return 39618;
            case 12:
                return 29866;
            case 394:
                return 35299;
            case 13:
                return 17396;

            //Patch 4
            case 380:
                return 14548;
            case 486:
                return 18839;
            case 15:
                return 15883;
            case 16:
                return 18533;
            case 502:
                return 12360;
            case 569:
                return 9662;

            //Patch 5
            case 18:
                return 7836;
            case 19:
                return 19898;
            case 20:
                return 16391;

            //Patch 6
            case 21:
                return 66357;
            case 22:
                return 24111;
            case 23:
                return 18049;

            //Patch 7
            case 409:
                return 66357;
            case 24:
                return 24111;
            case 469:
                return 18049;
            case 25:
                return 18049;

            //Patch 8
            case 26:
                return 12667;
            case 408:
                return 7759;
            case 559:
                return 30304;

            //Patch 9
            case 27:
                return 10977;
            case 376:
                return 11081;

            //Patch 10
            case 28:
                return 10084;
            case 412:
                return 6425;

            //Patch 11
            case 29:
                return 15635;
            case 30:
                return 9306;

            //Patch 12 - No loop

            //Patch 13 - No loop

            //Patch 14
            case 33:
                return 106094;
            case 34:
                return 52674;
            case 35:
                return 36568;

            //Patch 15 - No loop

            //Patch 16
            case 39:
                return 21467;
            case 40:
                return 8992;

            //Patch 17
            case 41:
                return 18469;
            case 42:
                return 12796;
            case 43:
                return 5719;

            //Patch 18
            case 10:
                return 9875;
            case 44:
                return 9510;
            case 45:
                return 11875;
            case 46:
                return 10744;
            case 47:
                return 3668;

            //Patch 19
            case 48:
                return 25414;
            case 49:
                return 21827;
            case 50:
                return 20456;
            case 51:
                return 18469;
            case 52:
                return 13790;

            //Patch 20
            case 53:
                return 17358;
            case 387:
                return 13107;
            case 54:
                return 9285;

            //Patch 21
            case 56:
                return 22432;
            case 57:
                return 10654;
            case 58:
                return 12915;
            case 59:
                return 6051;

            //Patch 22
            case 392:
                return 11779;
            case 61:
                return 10585;
            case 497:
                return 10340;
            case 520:
                return 8066;

            //Patch 23
            case 63:
                return 12317;
            case 64:
                return 6592;
            case 65:
                return 15437;

            //Patch 24
            case 66:
                return 18151;
            case 67:
                return 17916;
            case 68:
                return 12967;
            case 69:
                return 5371;

            //Patch 25
            case 70:
                return 36903;
            case 71:
                return 23413;
            case 72:
                return 21720;

            //Patch 26
            case 73:
                return 25234;

            //Patch 27
            case 74:
                return 55422;
            case 75:
                return 54632;
            case 76:
                return 41894;

            //Patch 28
            case 78:
                return 3289;

            //Patch 29
            case 79:
                return 31769;
            case 80:
                return 26242;
            case 81:
                return 24442;
            case 82:
                return 21972;

            //Patch 30
            case 83:
                return 46029;
            case 84:
                return 41492;
            case 85:
                return 16168;
            case 86:
                return 12039;

            //Patch 31
            case 406:
                return 57596;

            //Patch 32
            case 89:
                return 32490;
            case 88:
                return 40646;

            //Patch 33
            case 90:
                return 5974;

            //Patch 34
            case 91:
                return 12190;

            //Patch 35
            case 405:
                return 7298;

            //Patch 36
            case 93:
                return 23079;
            case 94:
                return 22210;

            //Patch 37
            case 95:
                return 22173;

            //Patch 38
            case 96:
                return 5132;

            //Patch 39
            case 417:
                return 73539;

            //Patch 40
            case 98:
                return 16467;
            case 99:
                return 6102;
            case 100:
                return 5814;

            //Patch 41
            case 101:
                return 8617;
            case 104:
                return 1974;

            //Patch 42
            case 410:
                return 36026;
            case 483:
                return 11617;
            case 533:
                return 30064;

            //Patch 43
            case 109:
                return 12326;
            case 382:
                return 13568;
            case 110:
                return 13800;

            //Patch 44
            case 111:
                return 33198;
            case 112:
                return 11865;
            case 171:
                return 11861;
            case 113:
                return 24580;
            case 398:
                return 40593;
            case 556:
                return 37458;

            //Patch 45 - No loop

            //Patch 46
            case 119:
                return 6456;
            case 120:
                return 7679;
            case 618:
                return 4599;
            case 121:
                return 6594;

            //Patch 47
            case 122:
                return 33710;
            case 123:
                return 16424;
            case 124:
                return 25424;
            case 360:
                return 71161;

            //Patch 48, 49
            case 386:
                return 35947;
            case 125:
                return 32584;
            case 420:
                return 30793;
            case 464:
                return 28616;
            case 126:
                return 34057;
            case 127:
                return 10544;
            case 128:
                return 16038;
            case 129:
                return 15068;

            //Patch 50
            case 130:
                return 10790;

            //Patch 51
            case 131:
                return 16134;

            //Patch 52
            case 378:
                return 20657;
            case 444:
                return 9870;
            case 501:
                return 11665;

            //Patch 53
            case 137:
                return 26853;
            case 138:
                return 13997;
            case 139:
                return 2325;

            //Patch 54
            case 140:
                return 23249;

            //Patch 55
            case 401:
                return 41538;
            case 141:
                return 35776;

            //Patch 56
            case 612:
                return 34056;
            case 613:
                return 33532;
            case 592:
                return 34094;
            case 605:
                return 41625;
            case 627:
                return 31030;
            case 614:
                return 41854;
            case 590:
                return 22829;

            //Patch 57
            case 581:
                return 53344;
            case 579:
                return 59332;
            case 582:
                return 73691;
            case 580:
                return 66676;
            case 577:
                return 42801;

            //Patch 58
            case 152:
                return 12160;

            //Patch 59
            case 391:
                return 12139;
            case 521:
                return 12740;
            case 554:
                return 26506;
            case 541:
                return 15801;

            //Patch 60
            case 157:
                return 10813;
            case 158:
                return 29768;
            case 159:
                return 13484;
            case 383:
                return 20035;

            //Patch 61
            case 402:
                return 28067;
            case 160:
                return 16120;
            case 161:
                return 37191;
            case 544:
                return 29436;

            //Patch 62
            case 162:
                return 25231;
            case 163:
                return 23838;

            //Patch 63
            case 164:
                return 12215;
            case 165:
                return 12096;

            //Patch 64
            case 166:
                return 21830;
            case 167:
                return 22862;
            case 168:
                return 12580;

            //Patch 65
            case 632:
                return 4402;
            case 604:
                return 4447;

            //Patch 66
            case 393:
                return 8910;
            case 457:
                return 4728;
            case 529:
                return 6017;
            case 454:
                return 7582;

            //Patch 67
            case 381:
                return 9476;
            case 571:
                return 6226;
            case 435:
                return 6508;

            //Patch 68
            case 178:
                return 39542;
            case 377:
                return 44897;
            case 181:
                return 63474;

            //Patch 69
            case 183:
                return 14702;
            case 184:
                return 6124;
            case 185:
                return 14719;

            //Patch 70
            case 186:
                return 53495;
            case 187:
                return 18976;

            //Patch 71
            case 188:
                return 25457;
            case 189:
                return 14889;
            case 190:
                return 20936;

            //Patch 72
            case 191:
                return 2916;
            case 192:
                return 9475;
            case 193:
                return 9888;

            //Patch 73
            case 194:
                return 13439;
            case 195:
                return 9850;
            case 196:
                return 15123;
            case 400:
                return 13951;

            //Patch 74
            case 416:
                return 20648;
            case 503:
                return 21581;
            case 557:
                return 30082;

            //Patch 75
            case 198:
                return 10742;

            //Patch 76
            case 199:
                return 13908;

            //Patch 77
            case 404:
                return 28418;
            case 491:
                return 24773;
            case 451:
                return 21293;
            case 507:
                return 16530;
            case 495:
                return 19547;

            //Patch 78
            case 202:
                return 2806;
            case 203:
                return 1376;

            //Patch 79
            case 388:
                return 3362;
            case 204:
                return 47895;

            //Patch 80
            case 411:
                return 29482;
            case 508:
                return 51399;
            case 418:
                return 24771;
            case 482:
                return 21964;
            case 568:
                return 19672;

            //Patch 81
            case 396:
                return 43734;
            case 441:
                return 52741;
            case 551:
                return 14559;

            //Patch 82
            case 205:
                return 13529;

            //Patch 83
            case 207:
                return 13529;

            //Patch 84 - Already looped

            //Patch 85
            case 208:
                return 21427;

            //Patch 86
            case 209:
                return 19462;
            case 210:
                return 13333;
            case 211:
                return 10238;

            //Patch 87
            case 399:
                return 19568;

            //Patch 88
            case 213:
                return 30670;
            case 214:
                return 23212;
            case 413:
                return 16661;

            //Patch 89
            case 215:
                return 33404;
            case 216:
                return 9313;

            //Patch 90
            case 217:
                return 28869;
            case 218:
                return 14867;

            //Patch 91
            case 219:
                return 27681;

            //Patch 92
            case 220:
                return 17544;

            //Patch 93
            case 221:
                return 13501;
            case 222:
                return 9496;

            //Patch 94
            case 223:
                return 18576;
            case 224:
                return 17698;

            //Patch 95
            case 225:
                return 36915;
            case 226:
                return 30800;
            case 227:
                return 23645;
            case 228:
                return 21270;
            case 229:
                return 17217;
            case 230:
                return 13791;

            //Patch 96
            case 231:
                return 12356;

            //Patch 97
            case 232:
                return 35585;
            case 233:
                return 17457;
            case 234:
                return 14614;
            case 235:
                return 14883;
            case 236:
                return 13993;

            //Patch 98
            case 237:
                return 29237;
            case 238:
                return 27141;
            case 239:
                return 26889;

            //Patch 99
            case 240:
                return 15804;

            //Patch 100
            case 241:
                return 18570;
            case 242:
                return 14714;
            case 243:
                return 11701;
            case 244:
                return 9829;

            //Patch 101
            case 390:
                return 9924;
            case 492:
                return 13018;
            case 245:
                return 10361;
            case 550:
                return 45340;
            case 522:
                return 8930;

            //Patch 102
            case 246:
                return 35142;
            case 247:
                return 28362;
            case 248:
                return 32702;

            //Patch 103
            case 249:
                return 17369;
            case 250:
                return 4791;

            //Patch 104
            case 251:
                return 16956;
            case 252:
                return 14818;

            //Patch 105
            case 253:
                return 16883;
            case 254:
                return 20718;
            case 255:
                return 15656;
            case 256:
                return 8497;

            //Patch 106
            case 257:
                return 17918;
            case 258:
                return 12392;
            case 259:
                return 18020;
            case 260:
                return 10335;

            //Patch 107 - No loop

            //Patch 108
            case 262:
                return 16321;

            //Patch 109
            case 589:
                return 17950;
            case 609:
                return 21506;
            case 623:
                return 8911;

            //Patch 110 - Already looped

            //Patch 111
            case 269:
                return 9767;
            case 270:
                return 10262;
            case 271:
                return 12348;
            case 272:
                return 11292;
            case 273:
                return 6225;

            //Patch 112
            case 379:
                return 3866;

            //Patch 113 - No loop

            //Patch 114 - No loop

            //Patch 115 - No loop

            //Patch 116
            case 279:
                return 1493;

            //Patch 117 - No loop

            //Patch 118 - No loop

            //Patch 119
            case 285:
                return 17055;

            //Patch 120 - No loop

            //Patch 121
            case 287:
                return 4811;

            //Patch 122 - No loop

            //Patch 123
            case 289:
                return 44252;

            //Patch 124 - No loop

            //Patch 125
            case 291:
                return 9944;

            //Patch 126 - No loop

            //Patch 127 - No loop

            //Patch 128
            case 395:
                return 22000;
            case 462:
                return 20501;
            case 487:
                return 17585;
            case 519:
                return 22925;
            case 423:
                return 19569;
            case 436:
                return 22048;
            case 526:
                return 14201;
            case 458:
                return 15188;
            case 461:
                return 24012;
            case 433:
                return 58997;

            //Patch 129 - No loop

            //Patch 136
            case 474:
                return 18655;
            case 542:
                return 18914;
            case 511:
                return 20984;

            //Patch 144
            case 480:
                return 18708;
            case 479:
                return 21606;
            case 518:
                return 18869;
            case 465:
                return 24187;
            case 477:
                return 23697;
            case 445:
                return 24291;
            case 523:
                return 27765;

            //Patch 152
            case 468:
                return 5580;
            case 531:
                return 9663;
            case 496:
                return 16104;
            case 505:
                return 19079;
            case 514:
                return 18042;
            case 553:
                return 20985;
            case 470:
                return 22153;
            case 490:
                return 18875;

            //Patch 153
            case 548:
                return 7067;
            case 449:
                return 517;
            case 555:
                return 11518;
            case 516:
                return 9821;
            case 434:
                return 9776;
            case 500:
                return 17898;
            case 509:
                return 3196;
            case 506:
                return 15599;
            case 567:
                return 8403;
            case 498:
                return 15252;
            case 564:
                return 18612;
            case 439:
                return 15535;
            case 471:
                return 17233;
            case 504:
                return 14407;

            //Patch 168
            case 448:
                return 13790;
            case 561:
                return 10343;
            case 473:
                return 11399;
            case 535:
                return 23448;

            //Patch 176
            case 384:
                return 23095;
            case 545:
                return 36069;

            //Patch 178
            case 622:
                return 77270;
            case 619:
                return 59352;
            case 610:
                return 41437;
            case 594:
                return 70282;

            //Patch 179 - No loop

            //Patch 184 - Already looped

            //Patch 255 - Already looped

            //Patch 256
            case 407:
                return 59046;
            case 467:
                return 52216;
            case 524:
                return 48730;
            case 570:
                return 51119;
            case 440:
                return 47382;
            case 565:
                return 51372;

            //Patch 257
            case 403:
                return 47127;
            case 453:
                return 61453;
            case 534:
                return 19652;
            case 450:
                return 55084;
            case 472:
                return 48103;
            case 515:
                return 37354;

            //Patch 258
            case 397:
                return 25958;
            case 442:
                return 19003;
            case 547:
                return 14722;
            case 572:
                return 16238;
            case 532:
                return 17284;

            //Patch 259 - No loop

            //Patch 260
            case 634:
                return 42299;

            //Patch 261, 262
            case 616:
                return 15347;
            case 608:
                return 16418;
            case 3750:
                return 45872;
            case 615:
                return 36241;
            case 638:
                return 38380;
            case 3749:
                return 26949;
            case 586:
                return 31169;
            case 620:
                return 42880;
            case 3754:
                return 43420;
            case 603:
                return 19631;
            case 637:
                return 30314;
            case 3746:
                return 43614;
            case 630:
                return 23213;
            case 611:
                return 52471;
            case 3747:
                return 46810;
            case 606:
                return 17945;

            //Patch 263
            case 635:
                return 26217;
            case 621:
                return 12784;
            case 596:
                return 16534;
            case 587:
                return 15176;
            case 599:
                return 8352;
            case 607:
                return 11115;

            //Patch 264
            case 584:
                return 41058;
            case 595:
                return 11833;
            case 598:
                return 11370;
            case 593:
                return 32199;
            case 597:
                return 22426;
            case 585:
                return 22133;

            //Patch 265
            case 617:
                return 47;

            //Patch 266
            case 135:
                return 59046;
            case 147:
                return 42463;
            case 274:
                return 42463;
            case 62:
                return 42463;
            case 103:
                return 41376;

            //Patch 267 - No loop

            //Patch 268 - No loop

            //Patch 269 - No loop

            //Patch 270 - No loop

            //Patch 271 - No loop

            //Patch 272 - No loop

            //Patch 273 - No loop

            //Patch 274 - No loop

            //Patch 275 - No loop

            //Patch 276 - No loop

            //Patch 277 - Already looped

            //Patch 278
            case 143:
                return 29216;
            case 295:
                return 41502;
            case 108:
                return 40681;
            case 77:
                return 39143;

            //Patch 279
            case 3753:
                return 23213;
            case 150:
                return 15033;
            case 87:
                return 24644;
            case 3752:
                return 35518;
            case 117:
                return 17588;
            case 142:
                return 24687;
            case 148:
                return 23482;
            case 3745:
                return 23990;
            case 180:
                return 17578;
            case 144:
                return 28903;
            case 3748:
                return 25809;
            case 115:
                return 16730;
            case 146:
                return 22909;
            case 151:
                return 16393;
            case 3751:
                return 18155;

            //Patch 280
            case 340:
                return 79757;
            case 341:
                return 222496;
            case 342:
                return 127505;

            //Patch 281
            case 356:
                return 77207;
            case 359:
                return 47380;

            //Patch 282
            case 354:
                return 43245;
            case 357:
                return 41724;
            case 361:
                return 33098;

            //Patch 283, 284
            case 362:
                return 41413;
            case 351:
                return 39957;
            case 352:
                return 31189;

            //Patch 285
            case 355:
                return 33980;
            case 353:
                return 23164;
            case 358:
                return 18321;

            //Patch 286
            case 366:
                return 30916;
            case 363:
                return 30356;
            case 364:
                return 14446;
            case 365:
                return 12916;

            //Patch 287
            case 1892:
                return 73949;
            case 339:
                return 128174;
            case 336:
                return 79408;
            case 337:
                return 59846;
            case 338:
                return 84533;

            //Patch 288
            case 1897:
                return 140874;
            case 1895:
                return 145076;
            case 1894:
                return 96017;
            case 1896:
                return 143352;
            case 1893:
                return 102204;

            //Patch 289 - No loop

            //Patch 290 - No loop

            //Patch 291 - No loop

            //Patch 292
            case 2883:
                return 31190;

            //Patch 293
            case 6405:
                return 40401;
            case 6393:
                return 30803;
            case 6403:
                return 38916;
            case 6396:
                return 37204;
            case 6398:
                return 37745;
            case 6394:
                return 34821;
            case 6402:
                return 37434;
            case 6408:
                return 35627;
            case 6406:
                return 35063;
            case 6410:
                return 36026;
            case 6409:
                return 36520;
            case 6399:
                return 34388;

            //Patch 294
            case 6401:
                return 30115;

            //Patch 300
            case 9725:
                return 56614;


        }

        return 0;
    }

    private static int getRS3LoopStart(int sampleID) {

        //Unfinished
        switch (sampleID) {

        }

        return 0;
    }
}

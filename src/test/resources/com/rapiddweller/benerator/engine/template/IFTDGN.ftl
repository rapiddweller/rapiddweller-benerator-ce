<#escape x as ediEscape(x)>
<#list iftdgns as iftdgn>
UNB+UNOA:2+CAPSTAN+HSDG+130821:1259+OPH'
UNH+${iftdgn.message_ref_no_E_0062_1_01}+IFTDGN:D:03A:UN:PROT20'
BGM+89N+${iftdgn.message_function_code_E_1225_2_03!}+${iftdgn.response_type_code_E_4343_2_04}'
HAN+L'
TDT+20+${iftdgn.vessel_voy_dir_vn_E_8028_10_02}${iftdgn.vessel_voy_dir_d_E_8028_10_02}+1+${iftdgn.vessel_id_E_8213_10_08_01}:CONTAINER VESSEL++++${iftdgn.vessel_id_E_8213_10_08_01}:::${iftdgn.vessel_name_E_8213_10_08_04}'
RFF+${iftdgn.ref_C_C506_12_01}'
LOC+153+${iftdgn.pol_ot_C_C517_14_02}:139:6'
DTM+132:130822:101'
DTM+133:130823:101'
LOC+61+${iftdgn.npoc_ot_C_C517_14_02}:139:6'
<#list iftdgn.equipmentDetails_L_EQD_22 as eqd>
EQD+CN+${eqd.equBbkRorItem_ot_C_C237_22_02}+${eqd.equipment_size_and_type_C_C224_22_03}'
MEA+WT+AAN+TNE:26'
</#list>
<#list iftdgn.consignmentInformations_L_CNI_25 as cni>
CNI+${cni_index+1}+${cni.bookingNo_ot_C_C503_25_02}'
HAN+L'
LOC+9+${cni.pol_ot_C_C517_28_02}:139:6'
LOC+11+${cni.pod_ot_C_C517_28_02}:139:6'
<#list cni.goodsItemDetails_L_GID_42 as gid>
GID+${gid.itemNumber_E_1496_42_01}+${gid.no_and_type_of_packages_C_C213_42_02}'
DGS+IMD+3+1263:24C+24:CEL+3+F-E'
FTX+AAD+++PAINT'
MEA+WT+AAL+KGM:21113'
MEA+WT+G+KGM:0'
RFF+APN:${gid.dgrNo_ot_C_C506_53_01}'
<#list gid.splitGoodsPlacements_L_SGP_55 as sgp>
SGP+${sgp.equBbkRorItem_ot_C_C237_55_01}'
LOC+147+${sgp.stowagePosition_ot_C_C517_56_02}'
MEA+WT+AAL+KGM:21113'
MEA+WT+G+KGM:0'
</#list>
</#list>
</#list>
UNT+${iftdgn.recursiveSegmentCount}+${iftdgn.message_ref_no_E_0062_1_01}'
UNZ+1+OPH'
</#list>
</#escape>
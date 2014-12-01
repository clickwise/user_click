<?php


class jbkw_preprocess{



function merge_keyword_relfiles($keyword_file_dir,$link_file_dir,$new_file_dir)
{

        $keyword_handle=opendir($keyword_file_dir);
        $keyword="";
        $uvs="";
        $ips="";
        $keyword_line="";
        $key_seg=array();
        $keyword_uvs_arr=array();
        $sel_num=0;
        while(($keyword_file=readdir($keyword_handle))!=false)
        {
                echo "keyword_file:".$keyword_file."\n";
                if($keyword_file!="."&&$keyword_file!=".."&&$keyword_file!="_SUCCESS"&&$keyword_file!="_logs")
                {
                        $keyword_in=fopen($keyword_file_dir."/".$keyword_file,'r');
                        while(!feof($keyword_in))
                        {
                                $keyword_line=trim(fgets($keyword_in,4096*1024));
                                $key_seg=explode("\001",$keyword_line);
                                if(count($key_seg)<3)
                                {
                                        continue;
                                }
                                $keyword=trim($key_seg[0]);
                                $uvs=trim($key_seg[1]);
                                $ips=trim($key_seg[2]);
                                if((!isset($keyword_uvs_arr[$keyword]))&&$ips>30)
                                {
                                        $sel_num++;
                                        $keyword_uvs_arr[$keyword]=$uvs;
                                }

                        }
                        fclose($keyword_in);
                }

        }
        echo "sel_num:".$sel_num."\n";

        $link_handle=opendir($link_file_dir);
        $link_keyword="";
        $link="";
        $link_uvs="";
        $link_ips="";
        $link_line="";
        $link_seg=array();
        $keyword_links_info_arr=array();


        while(($link_file=readdir($link_handle))!=false)
        {
                echo "link_file:".$link_file."\n";
                if($link_file!="."&&$link_file!=".."&&$link_file!="_SUCCESS"&&$link_file!="_logs")
                {
                        $link_in=fopen($link_file_dir."/".$link_file,'r');
                        while(!feof($link_in))
                        {
                                $link_line=trim(fgets($link_in,4096*64));
                                $link_seg=explode("\001",$link_line);
                                if(count($link_seg)<4)
                                {
                                        continue;
                                }
                                $link_keyword=trim($link_seg[0]);
                                $link=trim($link_seg[1]);
                                $link_uvs=trim($link_seg[2]);
                                $link_ips=trim($link_seg[3]);

                                if(!isset($keyword_links_info_arr[$link_keyword]))
                                {
                                        $keyword_links_info_arr[$link_keyword]=$link."\t".$link_uvs."\001";
                                }
                                else
                                {
                                        $keyword_links_info_arr[$link_keyword]=$keyword_links_info_arr[$link_keyword].$link."\t".$link_uvs."\001";
                                }
                        }
                }
        }


        if(!file_exists($new_file_dir)){
                mkdir($new_file_dir);
        }

        arsort($keyword_uvs_arr);
        $fout=fopen($new_file_dir."/0000",'w');

        $nklia="";
        $kw_num=0;
        $file_n=0;
        foreach($keyword_uvs_arr as $kk => $kv)
        {
                
                if($kw_num>10000)
                {
                   break;
                }
                $kw_num++;
                if($kw_num%1000==0)
                {
                  fclose($fout);
                  $file_n++;
                  $fout=fopen($new_file_dir."/000".$file_n,'w');   
                }
                $kk=trim($kk);
                if(isset($keyword_links_info_arr[$kk]))
                {
                        $nklia=getSortLinkArr($keyword_links_info_arr[$kk]);
                        if(!empty($nklia))
                        {
                                fwrite($fout,$kk."\001".$kv."\001".$nklia."\n");
                        }
                }
        }
        fclose($fout);

        if((!empty($keyword_file_dir))&&(!empty($link_file_dir))&&(strstr($keyword_file_dir,"/tmp/jbkw")!=false)&&(strstr($link_file_dir,"/tmp/jbkw")!=false))
        {
        system("rm -rf ".$keyword_file_dir);
        system("rm -rf ".$link_file_dir);
        }

}



function selectTopKwl($local_se_kwl,$jbkw_local_merge_dir)
{
   
	$kwl_handle=opendir($local_se_kwl);
	$kwl_line="";
	if(!file_exists($jbkw_local_merge_dir)){
		mkdir($jbkw_local_merge_dir);
	}
	$fout=fopen($jbkw_local_merge_dir."/0000",'w');
	$kln=1;
	$file_num=0;
	while(($kwl_file=readdir($kwl_handle))!=false)
	{

		if($kwl_file!="."&&$kwl_file!=".."&&$kwl_file!="_SUCCESS"&&$kwl_file!="_logs")
		{
			$kwl_in=fopen($local_se_kwl."/".$kwl_file,'r');
			while(!feof($kwl_in))
			{
				$kwl_line=trim(fgets($kwl_in,4096*64));
				if(empty($kwl_line))
				{
					continue;
				}

				fwrite($fout,$kwl_line."\n");
				if($kln%1000==0)
				{
					$file_num++;
					fclose($fout);
					$fout=fopen($jbkw_local_merge_dir."/000".$file_num,'w');                                  
				}

				if($kln>4000)
				{
					break;
				}
				$kln++;
			}
		}
	}
	fclose($fout);
	if((!empty($local_se_kwl))&&(strstr($local_se_kwl,"/tmp/jbkw")!=false))
	{
		system("rm -rf ".$local_se_kwl);
	}

}

function getSortLinkArr($klia)
{
	$new_klia="";
	$lsa=array();
	$seg_arr=explode("\001",$klia);
	if(count($seg_arr)<1)
	{
		return "";
	}

	$link_item="";
	$link="";
	$uvs=0;
	for($j=0;$j<count($seg_arr);$j++)
	{
		$link_item=trim($seg_arr[$j]);
		$temp_seg_arr=explode("\t",$link_item);
		if(count($temp_seg_arr)<2)
		{
			continue;
		}
		$link=trim($temp_seg_arr[0]);
		$uvs=trim($temp_seg_arr[1]);
		if(!isset($lsa[$link]))
		{
			$lsa[$link]=$uvs;
		}
	}

	arsort($lsa);
	if(count($lsa)<10)
	{
		foreach($lsa as $lk => $lv)
		{
			$new_klia=$new_klia.$lk."\t".$lv."\001";
		}
	}
	else
	{
		$tempc=0;
		foreach($lsa as $lk => $lv)
		{
			if($tempc>10)
			{
				break;
			}
			$tempc++;
			$new_klia=$new_klia.$lk."\t".$lv."\001";
		}

	}

	return $new_klia;

}





}





?>

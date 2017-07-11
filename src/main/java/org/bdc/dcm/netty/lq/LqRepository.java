package org.bdc.dcm.netty.lq;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bdc.dcm.netty.lq.config.InitPack;

public class LqRepository {

	private List<InitPack> lqInitPacks = new ArrayList<>();

	private final static LqRepository repository = new LqRepository();
	
	private LqRepository(){}
	
	public static LqRepository build(){
		return repository;
	}
	
	public void addOne(InitPack pack){
		if(!lqInitPacks.contains(pack))
			lqInitPacks.add(pack);
	}
	
	public InitPack findOne(String mac){
		Optional<InitPack> o = lqInitPacks.stream().filter(item->item.getMac().equals(mac)).findFirst();
		if(o.isPresent())
			return o.get();
		else
			return null;
	}
	public List<InitPack> findAll(){
		return new ArrayList<>(lqInitPacks);
	}
}

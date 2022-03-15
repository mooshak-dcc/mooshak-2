package pt.up.fc.dcc.mooshak.rest.contest.repository;

import java.util.ArrayList;
import java.util.List;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Contests;
import pt.up.fc.dcc.mooshak.managers.AdministratorManager;
import pt.up.fc.dcc.mooshak.rest.contest.model.ContestModel;
import pt.up.fc.dcc.mooshak.rest.exception.InternalServerException;
import pt.up.fc.dcc.mooshak.rest.exception.NotFoundException;
import pt.up.fc.dcc.mooshak.rest.repository.Repository;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Repository for contests
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 */
public class ContestRepository implements Repository<ContestModel> {
	private static final String CONTESTS_PATH = "data/contests";

	private static ContestRepository repository;

	private Contests contests;

	private ContestRepository() {

		try {
			contests = PersistentObject.openPath(CONTESTS_PATH);
		} catch (MooshakContentException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
	}

	public static ContestRepository getInstance() {
		if (repository == null)
			repository = new ContestRepository();
		return repository;
	}

	@Override
	public List<ContestModel> getAll() {
		
		List<Contest> contestList;
		try {
			contestList = contests.getChildren(true);
		} catch (MooshakContentException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
		
		List<ContestModel> contestModelList = new ArrayList<>();
		
		for (Contest contest : contestList) {
			ContestModel contestModel = new ContestModel();
			contestModel.copyFrom(contest);
			contestModelList.add(contestModel);
		}
		
		return contestModelList;
	}

	@Override
	public void add(ContestModel contestModel) {

		try {
			Contest contest = contests.create(contestModel.getId(), Contest.class);
			
			// create dependents
			AdministratorManager.getInstance().createDependants(contest);
			
			contestModel.copyTo(contest);
			
			contest.save();
		} catch (MooshakException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
		
	}

	@Override
	public void update(ContestModel contestModel, String id) {
		
		try {
			Contest contest = contests.open(id);
			contestModel.copyTo(contest);
			contest.save();
		} catch (MooshakException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
	}

	@Override
	public ContestModel get(String id) {
		
		Contest contest;
		try {
			contest = contests.open(id);
		} catch (MooshakException e) {
			throw new NotFoundException("The contest " + id + " does not exist.");
		}
		
		ContestModel contestModel = new ContestModel();
		contestModel.copyFrom(contest);
		
		return contestModel;
	}

	@Override
	public void delete(String id) {

		try {
			Contest contest = contests.open(id);
			contest.delete();
		} catch (MooshakException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
	}
}

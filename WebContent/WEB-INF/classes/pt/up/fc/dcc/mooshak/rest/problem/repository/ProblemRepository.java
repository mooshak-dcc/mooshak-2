package pt.up.fc.dcc.mooshak.rest.problem.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Contests;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Problems;
import pt.up.fc.dcc.mooshak.managers.AdministratorManager;
import pt.up.fc.dcc.mooshak.rest.exception.InternalServerException;
import pt.up.fc.dcc.mooshak.rest.exception.NotFoundException;
import pt.up.fc.dcc.mooshak.rest.problem.model.ProblemModel;
import pt.up.fc.dcc.mooshak.rest.repository.Repository;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Repository of problems
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 */
public class ProblemRepository implements Repository<ProblemModel> {

	private static Map<String, ProblemRepository> repositories = new HashMap<>();

	private Problems problems;

	private ProblemRepository(Contest contest) {

		try {
			problems = contest.open("problems");
		} catch (MooshakContentException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
	}

	public static ProblemRepository getInstance(Contest contest) {
		if (!repositories.containsKey(contest.getIdName()))
			repositories.put(contest.getIdName(), new ProblemRepository(contest));
		return repositories.get(contest.getIdName());
	}

	@Override
	public List<ProblemModel> getAll() {
		
		List<Problem> problemList;
		try {
			problemList = problems.getChildren(true);
		} catch (MooshakContentException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
		
		List<ProblemModel> problemModelList = new ArrayList<>();
		for (Problem problem : problemList) {
			ProblemModel problemModel = new ProblemModel();
			problemModel.copyFrom(problem);
			problemModelList.add(problemModel);
		}
		
		return problemModelList;
	}

	@Override
	public void add(ProblemModel problemModel) {

		try {
			Problem problem = problems.create(problemModel.getId(), Problem.class);
			
			// create dependents
			AdministratorManager.getInstance().createDependants(problem);
			
			problemModel.copyTo(problem);
			
			problem.save();
		} catch (MooshakException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
	}

	@Override
	public void update(ProblemModel problemModel, String id) {
		
		try {
			Problem problem = problems.open(id);
			problemModel.copyTo(problem);
			problem.save();
		} catch (MooshakException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
	}

	@Override
	public ProblemModel get(String id) {
		
		Problem problem;
		try {
			problem = problems.open(id);
		} catch (MooshakException e) {
			throw new NotFoundException("The problem " + id + " does not exist.");
		}
		
		ProblemModel problemModel = new ProblemModel();
		problemModel.copyFrom(problem);
		
		return problemModel;
	}

	@Override
	public void delete(String id) {

		try {
			Problem problem = problems.open(id);
			problem.delete();
		} catch (MooshakException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
	}
	
	
}

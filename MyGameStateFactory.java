package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import com.google.common.graph.Graph;
import com.google.common.graph.ValueGraphBuilder;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;
import uk.ac.bris.cs.scotlandyard.ui.model.PlayerProperty;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState> {


	@Nonnull @Override public GameState build(
			GameSetup setup,
			Player mrX,
			ImmutableList<Player> detectives) {
		// TODO

		if (setup == null) throw new NullPointerException("Game setup cannot be null!");

		if (mrX == null) throw new NullPointerException("Mr.x cannot be null!");

		if (mrX.isDetective()) throw new IllegalArgumentException("Mr.x cannot be detectives!");

		if (/*detectives == null || */detectives.isEmpty()) throw new NullPointerException("Detectives cannot be null!");

		Set<Piece> usedColors = new HashSet<>();
		Set<Integer> usedLocation = new HashSet<>();

		for (Player detective : detectives) {
			if (detective.isMrX()) throw new IllegalArgumentException("detectives cannot be Mr.x!");
			if (!usedColors.add(detective.piece())) throw new IllegalArgumentException("Same detectives are detected!");
			if (!usedLocation.add(detective.location()))
				throw new IllegalArgumentException("same locations are detected!");
			if (detective.tickets().getOrDefault(ScotlandYard.Ticket.SECRET, 0) > 0)
				throw new IllegalArgumentException("detectives cannot have secret tickets");
			if (detective.tickets().getOrDefault(ScotlandYard.Ticket.DOUBLE, 0) > 0)
				throw new IllegalArgumentException("detectives cannot have double tickets");
		}


		if (setup.moves.isEmpty()) throw new IllegalArgumentException("Moves cannot be empty");

		if (setup.graph.nodes().isEmpty()) throw new IllegalArgumentException("graph cannot be empty");

		//if(detectives.size() == 1) throw new IllegalArgumentException("detectives have to be more than 1");

		return new MyGameState(
				setup, mrX, detectives
		);
	}

	private static final class MyGameState implements GameState {
		private final GameSetup setup;
		private ImmutableSet<Piece> remaining;
		private ImmutableList<LogEntry> log;
		private final Player mrX;
		private final ImmutableList<Player> detectives;
		private ImmutableSet<Move> moves;
		private ImmutableSet<Piece> winner;

		private MyGameState(GameSetup setup, Player mrX, ImmutableList<Player> detectives) {
			this.setup = setup;
			this.mrX = mrX;
			this.detectives = detectives;
			this.remaining = ImmutableSet.of();
			this.log = ImmutableList.of();
			this.moves = ImmutableSet.of();
			this.winner = ImmutableSet.of();
		}


		@Nonnull
		@Override
		public GameState advance(Move move) {
			return null;
		}

		@Nonnull
		@Override
		public GameSetup getSetup() {
			return setup;
		}

		@Nonnull
		@Override
		public ImmutableSet<Piece> getPlayers() {
			return ImmutableSet.<Piece>builder()
					.add(mrX.piece())
					.addAll(detectives.stream().map(Player::piece).collect(Collectors.toSet()))
					.build();
		}

		@Nonnull
		@Override
		public Optional<Integer> getDetectiveLocation(Piece.Detective detective) {
			return detectives.stream()
					.filter(d -> d.piece().equals(detective))
					.map(Player::location)
					.findFirst();
		}

		@Nonnull
		@Override
		public Optional<Board.TicketBoard> getPlayerTickets(Piece piece) {
			for (Player detective : detectives)
				if(detective.piece().equals(piece))
					return Optional.of(new Board.TicketBoard() {
						@Override
						public int getCount(@Nonnull ScotlandYard.Ticket ticket) {
							return detective.tickets().getOrDefault(ticket, 0);
						}
					});

			if(mrX.piece().equals(piece))
				return Optional.of(new Board.TicketBoard() {
					@Override
					public int getCount(@Nonnull ScotlandYard.Ticket ticket) {
						return mrX.tickets().getOrDefault(ticket, 0);
					}
				});
			return Optional.empty();
		}

		@Nonnull
		@Override
		public ImmutableList<LogEntry> getMrXTravelLog() {
			return null;
		}

		@Nonnull
		@Override
		public ImmutableSet<Piece> getWinner() {
			return ImmutableSet.<Piece>builder().build();
		}

		@Nonnull
		@Override
		public ImmutableSet<Move> getAvailableMoves() {
			return null;
		}
	};
}




package service.bookings

import cats.effect.IO
import models._
import models.bookings.Confirmed
import models.bookings.errors.BookingNotFound
import repositories.BookingRepositoryAlgebra
import services.BookingServiceImpl
import weaver.SimpleIOSuite

import java.time.{LocalDate, LocalDateTime}

class MockBookingRepository extends BookingRepositoryAlgebra[IO] {

  private var bookings: Map[String, Booking] = Map.empty

  def withInitialBookings(initial: Map[String, Booking]): MockBookingRepository = {
    val repository = new MockBookingRepository
    repository.bookings = initial
    repository
  }

  override def getAllBookings: IO[List[Booking]] =
    IO.pure(bookings.values.toList)

  override def findBookingById(bookingId: String): IO[Option[Booking]] =
    IO.pure(bookings.get(bookingId))

  override def setBooking(booking: Booking): IO[Int] = {
    bookings += (booking.booking_id -> booking)
    IO.pure(1)
  }

  override def updateBooking(bookingId: String, updatedBooking: Booking): IO[Int] = {
    if (bookings.contains(bookingId)) {
      bookings += (bookingId -> updatedBooking)
      IO.pure(1)
    } else {
      IO.pure(0)
    }
  }

  override def deleteBooking(bookingId: String): IO[Int] = {
    if (bookings.contains(bookingId)) {
      bookings -= bookingId
      IO.pure(1)
    } else {
      IO.pure(0)
    }
  }

  override def doesOverlap(booking: Booking): IO[Boolean] =
    IO.pure(false)
}


object BookingServiceSpec extends SimpleIOSuite {

  def freshRepository = new MockBookingRepository

  // Sample booking data
  val sampleBooking: Booking = Booking(
    id = Some(1),
    booking_id = "booking_1",
    booking_name = "Sample Booking",
    user_id = 1,
    workspace_id = 1,
    booking_date = LocalDate.of(2024, 10, 10),
    start_time = LocalDateTime.of(2024, 10, 10, 9, 0),
    end_time = LocalDateTime.of(2024, 10, 10, 12, 0),
    status = Confirmed,
    created_at = LocalDateTime.now()
  )

  // Test case for creating a booking
  test("create a new booking successfully") {
    val mockRepository = freshRepository
    val bookingService = new BookingServiceImpl[IO](mockRepository)
    for {
      result <- bookingService.createBooking(sampleBooking)
    } yield expect(result == Right(1))
  }

  // Test case for finding a booking by ID
  test("find a booking by booking_id") {
    val mockRepository = freshRepository
    val bookingService = new BookingServiceImpl[IO](mockRepository)
    for {
      _ <- mockRepository.setBooking(sampleBooking) // Insert the booking
      result <- bookingService.findBookingById("booking_1")
    } yield expect(result == Right(sampleBooking))
  }

  // Test case for finding a booking that doesn't exist
  test("return an error if booking ID does not exist") {
    val mockRepository = freshRepository
    val bookingService = new BookingServiceImpl[IO](mockRepository)
    for {
      result <- bookingService.findBookingById("nonexistent_id")
    } yield expect(result == Left(BookingNotFound))
  }

  // Test case for updating a booking
  test("update a booking") {
    val mockRepository = freshRepository
    val bookingService = new BookingServiceImpl[IO](mockRepository)
    val updatedBooking = sampleBooking.copy(booking_name = "Updated Booking")
    for {
      _ <- mockRepository.setBooking(sampleBooking)
      result <- bookingService.updateBooking("booking_1", updatedBooking)
    } yield expect(result == Right(1))
  }

  // Test case for deleting a booking
  test("delete a booking") {
    val mockRepository = freshRepository
    val bookingService = new BookingServiceImpl[IO](mockRepository)
    for {
      _ <- mockRepository.setBooking(sampleBooking)
      result <- bookingService.deleteBooking("booking_1")
    } yield expect(result == Right(1))
  }
}

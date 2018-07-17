<?php

namespace Test;

use PHPUnit\Framework\TestCase;

use io\flexio\utils\TypedArray;
use org\generated\Book;
use org\generated\Person;
use org\generated\Review;
use org\generated\book\Reviews;
use org\generated\book\BookKind;
use org\generated\book\tags\BookTags;
use org\generated\person\Address;
use org\generated\book\Tags;
use \DateTime;

class BookTest extends TestCase {

    public function testPersonWithAddressAsEmbeddedSpec() {
        $person = new Person();
        $person -> withName( "personName" )
                -> withEmail( "person@mail.con" )
                -> withPostalCode( "postal.code" );

        $address = new Address();
        $address -> withStreetAddress( "12 street address" );

        $person -> withAddress( $address );

        $this -> assertSame( "personName", $person->name() );
        $this -> assertSame( "person@mail.con", $person->email() );
        $this -> assertSame( "12 street address", $person->address()->streetAddress() );
        $this -> assertSame( "postal.code", $person->postalCode() );
    }

    public function testBook() {
        $book = new Book();

        $person = new Person();
        $person -> withName( "personName" )
                -> withEmail( "person@mail.con" );

        $tags = new Tags();
        $tags[] = BookTags::SCIENCE();

        $book -> withName("bookName")
              -> withAuthor( $person )
              -> withBookFormat( "nul" )
              -> withKind( BookKind::TEXTBOOK() )
              -> withNumberOfPages( 999 )
              -> withPublished( true )
              -> withTags( $tags );

        $this -> assertSame( "bookName", $book->name() );
        $this -> assertSame( "nul", $book->bookFormat() );
        $this -> assertSame( "personName", $book->author()->name() );
        $this -> assertSame( "person@mail.con", $book->author()->email() );
        $this -> assertSame( "TEXTBOOK", $book->kind()->value() );
        $this -> assertSame( 999, $book->numberOfPages() );
        $this -> assertSame( true, $book->published() );
        $this -> assertSame( "SCIENCE", $book->tags()[0]->value() );
    }

    public function testReviewsList() {
        $book = new Book();

        $review = new Review();
        $review -> withReviewBody( "review body 1" );

        $book -> withReviews( new Reviews() );
        $book -> reviews()[] = $review;
        $book -> withKind( BookKind::valueOf("AUDIOBOOK") );

        $this -> assertSame( "review body 1", $book->reviews()[0]->reviewBody() );
        $this -> assertSame( "AUDIOBOOK", $book->kind()->value() );
    }

    public function testReviewValueSpecTypedAttribute(){
        $review = new Review();

        $author = new Person();
        $author -> withName( "name" );

        $review -> withAuthor( $author );

        $this -> assertSame( "name", $review->author()->name() );
    }

    public function testDate() {
        $review = new Review();

        $date = new DateTime('2011-08-01T15:03:01.012345Z');
        $datetime = new DateTime('2011-09-01T15:04:01.012345Z');
        $time = new DateTime('2011-10-01T15:05:01.012345Z');

        $review -> withPublicationDate( $date );
        $review -> withPublicationDateTime( $datetime );
        $review -> withPublicationTime( $time );

        $this -> assertSame( "2011-08-01", $review->publicationDate()->format("Y-m-d") );
        $this -> assertSame( "2011-09-01", $review->publicationDateTime()->format("Y-m-d") );
        $this -> assertSame( "2011-10-01", $review->publicationTime()->format("Y-m-d") );
    }

    public function testExternalValueObject(){
        $person = new Person();

        $typedArray = new TypedArray(Review::class);

        $person -> withReference( $typedArray );

        $review = new Review();
        $review -> withReviewBody( "coucou body" );

        $review_two = new Review();
        $review_two -> withReviewBody( "hello nobody" );

        $person -> reference()[] = $review;
        $typedArray[]= $review_two;

        $this -> assertSame( "coucou body", $person -> reference()[0]->reviewBody() );
        $this -> assertSame( "hello nobody", $person -> reference()[1]->reviewBody() );
    }

}